#ifndef _LLC_H
#define _LLC_H

extern void llc_init(void);
extern int llc_read(void);
extern void llc_write(unsigned char b);
extern short llc_data_available(void);
extern void llc_discard(void);

#endif

