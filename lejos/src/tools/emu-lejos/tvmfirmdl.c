/**
 * Jose's changes: 
 * -- Increased size of srec structure to allow for longer lines.
 * -- Minor changes to a couple of error messages.
 */

/*
 *  firmdl.c
 *
 *  A hack to download firmware to the RCX.
 *
 *  usage: firmdl Firm0309.lgo
 *
 *  Under IRIX, Linux, and Solaris, you should be able to compile this
 *  program with cc firmdl.c -o firmdl.  I don't know about other versions
 *  of Unix, although I'd be interested in hearing about compatibility
 *  issues that you are able to fix.
 *
 *  Set DEFAULTTTY to the serial device you want to use.
 *  Set the RCXTTY environment variable to override DEFAULTTTY.
 *
 *  Based on send.c and srec.c.  Maybe someday I will distribute my tools
 *  as multiple files...
 *
 *  Some additional documentation is available at:
 *
 *     http://graphics.stanford.edu/~kekoa/rcx/tools.html
 *
 *  Acknowledgements:
 *
 *     Laurent Demailly pointed out I didn't transfer some fixes from
 *        send.c over to this file.  He also mentioned that this program
 *        compiles fine under Solaris 2.6.
 *     Allen Martin mentioned his modification of not sending all 4K if the
 *        firmware is shorter than that.  I discovered that the correct way 
 *        to do this was to send everything but the trailing zero bytes,
 *        and before I had a chance to implement this, Markus Noga sent me
 *        the changes needed to implement this.  I incorporated the changes
 *        with modifications, plus a few others to make the software a bit
 *        more robust.
 *     Markus forwarded a message from Gavin Smyth that pointed out a
 *        problem with an uninitialized variable.  Gavin also pointed out
 *        that this program compiles fine under Cygwin.
 *     In a separate message, Gavin suggested a small change to shorten the
 *        0.3 ms pause during the download.
 *     Luis Villa noticed a problem with certain s-record files; the
 *        problem turned out to be long S0 records generated by the linker.
 *        Changed the source to allow these (improper?) records.
 */

/*
 *  Copyright (C) 1998, 1999, Kekoa Proudfoot.  All Rights Reserved.
 *
 *  License to copy, use, and modify this software is granted provided that
 *  this notice is retained in any copies of any part of this software.
 *
 *  The author makes no guarantee that this software will compile or
 *  function correctly.  Also, if you use this software, you do so at your
 *  own risk.
 * 
 *  Kekoa Proudfoot
 *  kekoa@graphics.stanford.edu
 *  10/3/98
 */

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdlib.h>
#include <unistd.h>
#include <termios.h>
#include <stdio.h>
#include <string.h>
#include <sys/time.h>
#include <ctype.h>
#include <string.h>

#if defined(LINUX)
#define DEFAULTTTY   "/dev/ttyS0" /* Linux - COM1 */
#elif defined(WINNT)
#define DEFAULTTTY   "com1"       /* Cygwin - COM1 */
#else
#define DEFAULTTTY   "/dev/ttyd2" /* IRIX - second serial port */
#endif

char *progname;

/* RCX routines */

#define BUFFERSIZE   4096
#define RETRIES      5

int
rcx_init(char *tty)
{
    int fd;
    struct termios ios;

    if ((fd = open(tty, O_RDWR)) < 0) {
	perror("open");
	exit(1);
    }

    if (!isatty(fd)) {
	close(fd);
	fprintf(stderr, "%s: not a tty\n", tty);
	exit(1);
    }

    memset(&ios, 0, sizeof(ios));
    ios.c_cflag = CREAD | CLOCAL | CS8 | PARENB | PARODD;
    cfsetispeed(&ios, B2400);
    cfsetospeed(&ios, B2400);

    if (tcsetattr(fd, TCSANOW, &ios) == -1) {
	perror("tcsetattr");
	exit(1);
    }

    return fd;
}

void
rcx_close(int fd)
{
    close(fd);
}

int
rcx_send(int fd, unsigned char *sbuf, int slen, unsigned char *rbuf, int rlen)
{
    unsigned char tbuf[BUFFERSIZE];
    unsigned char vbuf[BUFFERSIZE];
    unsigned char *sp = sbuf;
    struct timeval tv;
    fd_set fds;
    int tlen = 0, vlen, vpos, rpos;
    int sum = 0, retry, returnval, count;

    tbuf[tlen++] = 0x55;
    tbuf[tlen++] = 0xff;
    tbuf[tlen++] = 0x00;
    while (slen--) {
	tbuf[tlen++] = *sp;
	tbuf[tlen++] = (~*sp) & 0xff;
	sum += *sp++;
    }
    tbuf[tlen++] = sum;
    tbuf[tlen++] = ~sum;

    for (retry = 0; retry < RETRIES; retry++) {
	if (write(fd, tbuf, tlen) != tlen) {
	    perror("write");
	    exit(1);
	}

	vlen = 0;
	while (vlen < tlen + 5 + 2 * rlen) {
	    FD_ZERO(&fds);
	    FD_SET(fd, &fds);
	    tv.tv_sec = 0;
	    tv.tv_usec = 300000;
	    if (select(FD_SETSIZE, &fds, NULL, NULL, &tv) == -1) {
		perror("select");
		exit(1);
	    }
	    if (!FD_ISSET(fd, &fds))
		break;
	    if ((count = read(fd, &vbuf[vlen], BUFFERSIZE - vlen)) == -1) {
		perror("read");
		exit(1);
	    }
	    vlen += count;
	}

	/* Check echo */

	returnval = -2;
	if (vlen < tlen)
	    continue; /* retry */
	for (vpos = 0; vpos < tlen; vpos++)
	    if (tbuf[vpos] != vbuf[vpos])
		break;
	if (vpos < tlen)
	    continue; /* retry */

	/* Check reply */

	returnval = 0;
	if (vpos == vlen)
	    break; /* could continue instead */

	returnval = -1;
	if (vlen - vpos < 5)
	    break; /* could continue instead */

	if (vbuf[vpos++] != 0x55)
	    break; /* could continue instead */
	if (vbuf[vpos++] != 0xff)
	    break; /* could continue instead */
	if (vbuf[vpos++] != 0x00)
	    break; /* could continue instead */

	for (sum = 0, rpos = 0; vpos < vlen - 2; vpos += 2, rpos++) {
	    if (vbuf[vpos] != ((~vbuf[vpos+1]) & 0xff))
		break;
	    sum += vbuf[vpos];
	    if (rpos < rlen)
		rbuf[rpos] = vbuf[vpos];
	}
	if (vpos != vlen - 2)
	    break; /* could continue instead */
	if (vbuf[vpos] != ((~vbuf[vpos+1]) & 0xff))
	    break; /* could continue instead */
	if ((sum & 0xff) != vbuf[vpos])
	    break; /* could continue instead */

	return rpos;
    }

    return returnval;
}

/* S-record routines */

/* srec.h */

typedef struct {
    unsigned char type;
    unsigned long addr;
    unsigned char count;
    unsigned char data[88];
} srec_t;

#define S_OK               0
#define S_NULL            -1
#define S_INVALID_HDR     -2
#define S_INVALID_CHAR    -3
#define S_INVALID_TYPE    -4
#define S_TOO_SHORT       -5
#define S_TOO_LONG        -6
#define S_INVALID_CKSUM   -7

extern int srec_decode(srec_t *srec, char *line);
extern int srec_encode(srec_t *srec, char *line);

/* srec.c */

static signed char ctab[256] = {
    -1,-1,-1,-1,-1,-1,-1,-1,  -1,-1,-1,-1,-1,-1,-1,-1,
    -1,-1,-1,-1,-1,-1,-1,-1,  -1,-1,-1,-1,-1,-1,-1,-1,
    -1,-1,-1,-1,-1,-1,-1,-1,  -1,-1,-1,-1,-1,-1,-1,-1,
     0, 1, 2, 3, 4, 5, 6, 7,   8, 9,-1,-1,-1,-1,-1,-1,
     0,10,11,12,13,14,15,-1,  -1,-1,-1,-1,-1,-1,-1,-1,
    -1,-1,-1,-1,-1,-1,-1,-1,  -1,-1,-1,-1,-1,-1,-1,-1,
     0,10,11,12,13,14,15,-1,  -1,-1,-1,-1,-1,-1,-1,-1,
    -1,-1,-1,-1,-1,-1,-1,-1,  -1,-1,-1,-1,-1,-1,-1,-1,
    -1,-1,-1,-1,-1,-1,-1,-1,  -1,-1,-1,-1,-1,-1,-1,-1,
    -1,-1,-1,-1,-1,-1,-1,-1,  -1,-1,-1,-1,-1,-1,-1,-1,
    -1,-1,-1,-1,-1,-1,-1,-1,  -1,-1,-1,-1,-1,-1,-1,-1,
    -1,-1,-1,-1,-1,-1,-1,-1,  -1,-1,-1,-1,-1,-1,-1,-1,
    -1,-1,-1,-1,-1,-1,-1,-1,  -1,-1,-1,-1,-1,-1,-1,-1,
    -1,-1,-1,-1,-1,-1,-1,-1,  -1,-1,-1,-1,-1,-1,-1,-1,
    -1,-1,-1,-1,-1,-1,-1,-1,  -1,-1,-1,-1,-1,-1,-1,-1,
    -1,-1,-1,-1,-1,-1,-1,-1,  -1,-1,-1,-1,-1,-1,-1,-1,
};

static int ltab[10] = {4,4,6,8,0,4,0,8,6,4};

#define C1(l,p)  (ctab[l[p]])
#define C2(l,p)  ((C1(l,p)<<4)|C1(l,p+1))

int
srec_decode(srec_t *srec, char *_line)
{
    int len, pos = 0, count, alen, sum = 0;
    unsigned char *line = (unsigned char *)_line;

    if (!srec || !line)
	return S_NULL;

    for (len = 0; line[len]; len++)
	if (line[len] == '\n' || line[len] == '\r')
	    break;

    if (len < 4)
	return S_INVALID_HDR;

    if (line[0] != 'S')
	return S_INVALID_HDR;

    for (pos = 1; pos < len; pos++) {
	if (C1(line, pos) < 0)
	    return S_INVALID_CHAR;
    }

    srec->type = C1(line, 1);
    count = C2(line, 2);

    if (srec->type > 9)
	return S_INVALID_TYPE;
    alen = ltab[srec->type];
    if (alen == 0)
	return S_INVALID_TYPE;
    if (len < alen + 6 || len < count * 2 + 4)
	return S_TOO_SHORT;
    if (count > 87 || len > count * 2 + 4)
	return S_TOO_LONG;

    sum += count;

    len -= 4;
    line += 4;

    srec->addr = 0;
    for (pos = 0; pos < alen; pos += 2) {
	unsigned char value = C2(line, pos);
	srec->addr = (srec->addr << 8) | value;
	sum += value;
    }

    len -= alen;
    line += alen;

    for (pos = 0; pos < len - 2; pos += 2) {
	unsigned char value = C2(line, pos);
	srec->data[pos / 2] = value;
	sum += value;
    }

    srec->count = count - (alen / 2) - 1;

    sum += C2(line, pos);

    if ((sum & 0xff) != 0xff)
	return S_INVALID_CKSUM;

    return S_OK;
}

int
srec_encode(srec_t *srec, char *line)
{
    int alen, count, sum = 0, pos;

    if (srec->type > 9)
	return S_INVALID_TYPE;
    alen = ltab[srec->type];
    if (alen == 0)
	return S_INVALID_TYPE;

    line += sprintf(line, "S%d", srec->type);

    if (srec->count > 32)
	return S_TOO_LONG; 
    count = srec->count + (alen / 2) + 1;
    line += sprintf(line, "%02X", count);
    sum += count;

    while (alen) {
	int value;
	alen -= 2;
	value = (srec->addr >> (alen * 4)) & 0xff;
	line += sprintf(line, "%02X", value);
	sum += value;
    }

    for (pos = 0; pos < srec->count; pos++) {
	line += sprintf(line, "%02X", srec->data[pos]);
	sum += srec->data[pos];
    }

    sprintf(line, "%02X\n", (~sum) & 0xff);

    return S_OK;
}

#define IMAGE_START   0x8000
#define IMAGE_LEN     0x4c00
#define IMAGE_END     (IMAGE_START + IMAGE_LEN)
#define TRANSFER_SIZE 0xc8

#ifdef  FORCE_NO_ZERO_PADDING
#define STRIP_ZEROS   1
#else
#define STRIP_ZEROS   0
#endif

int
main(int argc, char **argv)
{
    unsigned char image[IMAGE_LEN];
    unsigned char send[BUFFERSIZE];
    unsigned char recv[BUFFERSIZE];
    char buf[256];
    FILE *file;
    srec_t srec;
    int line = 0;
    unsigned short cksum = 0;
    int addr, index, size, i;
    char *tty;
    char *fileName;
    char *tinyvmHome;
    int fd;
    int length = 0;
    int strip = STRIP_ZEROS;
    unsigned short image_start = IMAGE_START;

    progname = argv[0];

    if ((tinyvmHome = getenv("TINYVM_HOME")) == NULL) {
	fprintf(stderr, "Your TINYVM_HOME variable is undefined.\n");
	exit(1);
    }
    
    if (argc == 1)
    {
      fileName = (char *) malloc (strlen (tinyvmHome) + 32);
      strcpy (fileName, tinyvmHome);
      strcat (fileName, "/bin/lejos.srec");
      printf ("Firmware file: %s\n", fileName);
    }      
    else if (argc == 2)
    {
      fileName = argv[1];
    }
    else
    {
      fprintf(stderr, "Use: %s [filename]\n", argv[0]);
      exit(1);
    }

    if ((file = fopen(fileName, "r")) == NULL) {
	fprintf(stderr, "%s: failed to open\n", fileName);
	exit(1);
    }

    /* Build an image of the srecord data */

    memset(image, 0, sizeof(image));

    while (fgets(buf, sizeof(buf), file)) {
	int error;
	line++;
	/* Skip blank lines */
	for (i = 0; buf[i]; i++)
	    if (buf[i] != ' ' && buf[i] != '\t' && buf[i] != '\n' &&
		buf[i] != '\r')
		break;
	if (!buf[i])
	    continue;
	if ((error = srec_decode(&srec, buf)) < 0) {
	    char *errstr = NULL;
	    switch (error) {
	    case S_NULL: errstr = "null string error"; break;
	    case S_INVALID_HDR: errstr = "invalid header"; break;
	    case S_INVALID_CHAR: errstr = "invalid character"; break;
	    case S_INVALID_TYPE: errstr = "invalid type"; break;
	    case S_TOO_SHORT: errstr = "line too short"; break;
	    case S_TOO_LONG: errstr = "line too long"; break;
	    case S_INVALID_CKSUM: break; /* ignore these */
	    default: errstr = "unknown error"; break;
	    }
	    if (errstr) {
		fprintf(stderr, "%s: %s on line %d\n", fileName, errstr, line);
		exit(1);
	    }
	}
	if (srec.type == 0) {
	    if (srec.count == 16)
		if (!strncmp(srec.data, "?LIB_VERSION_L00", 16))
		    strip = 1;
	}
	else if (srec.type == 1) {
	    if (srec.addr < IMAGE_START || srec.addr + srec.count > IMAGE_END){
		fprintf(stderr, "%s: address out of bounds on line %d\n",
			fileName, line);
		exit(1);
	    }
	    if (!strip && (srec.addr + srec.count - IMAGE_START > length))
		length = srec.addr + srec.count - IMAGE_START;
	    memcpy(&image[srec.addr - IMAGE_START], &srec.data, srec.count);
	}
	else if (srec.type == 9) {
	    if (srec.addr < IMAGE_START || srec.addr > IMAGE_END) {
		fprintf(stderr, "%s: address out of bounds on line %d\n",
			fileName, line);
		exit(1);
	    }
	    image_start = srec.addr;
	}
    }

    /* Find image length */

    /* Not entirely legal if firmware expects zeros to be there */
    /* GCC does not generate unnecessary padding, skip if not Firm0309.lgo */
    /* Define FORCE_NO_ZERO_PADDING to force the zero stripping to occur */
    /* You will want to do this if you pad with zeros to be OCX compatible */

    if (strip) {
	for (length = IMAGE_LEN - 1; length >= 0 && image[length]; length--);
	length++;
    }

    if (length == 0) {
	fprintf(stderr, "Image contains no data\n");
	exit(1);
    }

    /* Checksum it */

    for (i = 0; i < length; i++)
	cksum += image[i];

    /* Open the serial port */

    if ((tty = getenv("RCXTTY")) == NULL) {
	fprintf(stderr, "Your RCXTTY variable is undefined. It must be defined as the IR device (e.g. /dev/ttyS0, /dev/ttyS1, com1, com2, etc.)\n");
	exit(1);
    }

    fd = rcx_init(tty);

    /* Delete firmware */

    send[0] = 0x65;
    send[1] = 1;
    send[2] = 3;
    send[3] = 5;
    send[4] = 7;
    send[5] = 11;
	
    for (i = 0; i < 5; i++) {
	if (rcx_send(fd, send, 6, recv, 1) == 1)
	    break;
    }
    if (i == 5) {
	fprintf(stderr, "%s: Delete firmware failed.\n", argv[0]);
	exit(1);
    }

    /* Start firmware download */

    send[0] = 0x75;
    send[1] = (image_start >> 0) & 0xff;
    send[2] = (image_start >> 8) & 0xff;
    send[3] = (cksum >> 0) & 0xff;
    send[4] = (cksum >> 8) & 0xff;
    send[5] = 0;

    for (i = 0; i < 5; i++) {
	if (rcx_send(fd, send, 6, recv, 2) == 2 && recv[1] == 0)
	    break;
    }
    if (i == 5) {
	fprintf(stderr, "%s: Start firmware download failed.\n", progname);
	exit(1);
    }

    /* Transfer data */

    addr = 0;
    index = 1;
    for (addr = 0, index = 1; addr < length; addr += size, index++) {
	size = IMAGE_LEN - addr;
	send[0] = 0x45;
	if (index & 1)
	    send[0] |= 0x08;
	if (size > TRANSFER_SIZE)
	    size = TRANSFER_SIZE;
	else
	    index = 0;
	send[1] = (index >> 0) & 0xff;
	send[2] = (index >> 8) & 0xff;
	send[3] = (size >> 0) & 0xff;
	send[4] = (size >> 8) & 0xff;
	memcpy(&send[5], &image[addr], size);
	for (i = 0, cksum = 0; i < size; i++)
	    cksum += send[5 + i];
	send[size + 5] = cksum & 0xff;
	for (i = 0; i < 5; i++) {
	    if (rcx_send(fd, send, size + 6, recv, 2) == 2 && recv[1] == 0)
		break;
	}
	if (i == 5) {
	    fprintf(stderr, "%s: Transfer data failed. Check range of IR tower or batteries.\n", progname);
	    exit(1);
	}
    }

    /* Unlock firmware */

    send[0] = 0xa5;
    send[1] = 76;
    send[2] = 69;
    send[3] = 71;
    send[4] = 79;
    send[5] = 174;

    for (i = 0; i < 5; i++) {
	if (rcx_send(fd, send, 6, recv, 26) == 26)
	    break;
    }
    if (i == 5) {
	fprintf(stderr, "%s: Unlock firmware failed.\n", progname);
	exit(1);
    }

    rcx_close(fd);

    exit(0);
}
