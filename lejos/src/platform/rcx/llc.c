//
// Serial port
//

// Serial receive data register 
extern unsigned char S_RDR;

// Serial transmit data register
extern unsigned char S_TDR;

// Serial mode register
extern unsigned char S_MR;

// Serial control register
extern unsigned char S_CR;

#define SCR_TX_IRQ	0x80	   // Transmit irq enable
#define SCR_RX_IRQ	0x40	   // Receive / recv err irq enable
#define SCR_TRANSMIT	0x20	   // Enable transmission
#define SCR_RECEIVE	0x10	   // Enable receiving
#define SCR_TE_IRQ	0x04	   // Transmit end irq enable

// Serial status register
extern unsigned char S_SR;

#define SSR_TRANS_EMPTY	0x80	   // Transmit buffer empty
#define SSR_RECV_FULL	0x40	   // Receive buffer full
#define SSR_OVERRUN_ERR	0x20	   // Overrun error
#define SSR_FRAMING_ERR	0x10	   // Framing error
#define SSR_PARITY_ERR	0x08	   // Parity error
#define SSR_ERRORS      0x38       // All errors
#define SSR_TRANS_END	0x04	   // Transmission end because buffer empty

// Serial baud rate register
extern unsigned char S_BRR;

// Ports 4 and 5

// Port 4 data direction register
extern unsigned char PORT4_DDR;

// Port 4 I/O register
extern unsigned char PORT4;

// Port 5 data direction register
extern unsigned char PORT5_DDR;

extern unsigned char rom_port4_ddr;	//!< ROM shadow of port 4 DDR
extern unsigned char rom_port5_ddr;	//!< ROM shadow of port 5 DDR

// Timer 1

// Timer 1 control register
extern unsigned char T1_CR;

// Timer 1 control / status register
extern unsigned char T1_CSR;

// Timer 1 constant A register
extern          unsigned char T1_CORA;

// IRQ Vectors

extern void *eri_vector;        // ERI interrupt vector
extern void *rxi_vector;        // RXI interrupt vector
extern void *txi_vector;        // TXI interrupt vector
extern void *tei_vector;        // TEI interrupt vector

#define MAX_BUFFER 32

static int sending;		// transmission state
static unsigned char send_byte;
static unsigned char buffer[MAX_BUFFER];
static int start, next;

///////////////////////////////////////////////////////////////////////////////
//
// Functions
//
///////////////////////////////////////////////////////////////////////////////

#define HANDLER_WRAPPER(wrapstring,handstring) \
__asm__ (".text\n.align 1\n.global _" wrapstring "\n_" wrapstring \
": push r0\npush r1\npush r2\npush r3\njsr @_" handstring \
"\npop r3\npop r2\npop r1\npop r0\nrts\n")

void llc_rx_handler(void);
void llc_rxerror_handler(void);
void llc_tx_handler(void);
void llc_txend_handler(void);
void llc_show(short aValue);

void llc_init(void) {
  S_CR = 0;                      // Serial Control Register	
  T1_CR = 0;                     // Timer 1 Control Register
  T1_CSR = 0;                    // Timer1 Control Status Register
  S_MR = 0;                      // Serial Mode Register, no parity
  S_BRR = 207;                   // Serial Baud Rate Register - set to 2400 (slow)
  S_SR = 0;                      // Serial Status Register
  PORT4 &= ~1;                   // Port 4 I/O Register - short range = 0
  sending = 0;                   // Not transmitting
  rom_port4_ddr |= 1;	         // Rom Port4 Data Direction Shadow Register
  PORT4_DDR = rom_port4_ddr;     // Port 4 Data Direction Register
  T1_CR  = 0x9;                  // Enable carrier frequency
  T1_CSR = 0x13;
  T1_CORA = 0x1a;
  rom_port5_ddr = 4;	
  PORT5_DDR = rom_port5_ddr;
  eri_vector = &llc_rxerror_handler;
  rxi_vector = &llc_rx_handler;
  txi_vector = &llc_tx_handler;
  tei_vector = &llc_txend_handler;
  S_CR = SCR_RECEIVE | SCR_RX_IRQ; // Allow Receives
  start = next = 0;
}

void llc_write(unsigned char b) {
  send_byte = b;
  sending = 1;
  S_SR &= ~(SSR_TRANS_EMPTY | SSR_TRANS_END);	  // clear flags
  S_CR |= SCR_TRANSMIT | SCR_TX_IRQ | SCR_TE_IRQ; // enable transmit & irqs
}

unsigned int llc_read(void) {
  if (next != start) {
    unsigned char b = buffer[start];
    if (start++ == MAX_BUFFER) start = 0;
    return b;
  } else {
    return -1;
  } 
}

short llc_data_available() {
  return (next != start ? 1 : 0);
} 


// The byte received interrupt handler

HANDLER_WRAPPER("llc_rx_handler","llc_rx_core");
void llc_rx_core(void) {
  if(!sending) {
    // received a byte from PC
    buffer[next] = S_RDR;
    if (next++ == MAX_BUFFER) next = 0;
  } else {
    // echos of own bytes -> collision detection
    if(S_RDR != send_byte) llc_txend_handler();
    sending = 0;
  }
  S_SR &= ~SSR_RECV_FULL;
}

// The receive error interrupt handler

HANDLER_WRAPPER("llc_rxerror_handler","llc_rxerror_core");
void llc_rxerror_core(void) {
  if(sending) llc_txend_handler(); // Force end of transmission
  S_SR &= ~SSR_ERRORS;             // Clear error
}

// End-of-transmission interrupt handler
HANDLER_WRAPPER("llc_txend_handler","llc_txend_core");
void llc_txend_core(void) {
  S_CR &= ~(SCR_TX_IRQ | SCR_TRANSMIT | SCR_TE_IRQ); // Disable transmit
  S_SR &= ~(SSR_TRANS_EMPTY | SSR_TRANS_END);        // Clear transmit status flags
}

// The transmit byte interrupt handler
// Write next byte if there's one left, otherwise unhook irq.
HANDLER_WRAPPER("llc_tx_handler","llc_tx_core");
void llc_tx_core(void) {
  if(sending == 1) {
    S_TDR = send_byte ;      // transmit byte
    sending = 2;             // Sent but not validated
    S_SR &= ~SSR_TRANS_EMPTY;
  } else {
    S_CR &= ~SCR_TX_IRQ;     // disable transmission interrupt
  }
}

void llc_show(short aValue) {
    __rcall3 ((short) 0x1ff2, (short) 0x301f, (short) aValue, (short) 0x3002);
    __rcall0 ((short) 0x27c8);
}


