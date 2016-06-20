#ifndef _UART_H_
#define _UART_H_

#include <stdio.h>

void initUART();

int uartPutChar(char c, FILE *stream);
char uartGetChar(FILE *stream);

//static FILE myStream;

#endif
