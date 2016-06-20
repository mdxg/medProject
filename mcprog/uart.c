#include "uart.h"
#include <avr/io.h>
#include <stdio.h>

static FILE myStream = FDEV_SETUP_STREAM(uartPutChar, uartGetChar, _FDEV_SETUP_RW);

void initUART(){
	UCSRA = 0x00;
	UCSRB = (1<<RXEN) | (1<<TXEN);
	UCSRC = (1<<URSEL) | (1<<UCSZ1) | (1<<UCSZ0);
	UBRRH = 0x00;
	UBRRL = 0x03;

	stdout = &myStream;
	stdin = &myStream;
}

int uartPutChar(char c, FILE *stream){
	UDR = c;
	while(!(UCSRA & (1<<UDRE)));
	return 0;
}

char uartGetChar(FILE *stream){
	while(!(UCSRA & (1<<RXC)));
	return UDR;
}


