#define F_CPU 14745600
#include <avr/io.h>

#include <util/delay.h>
#include <stdio.h>

#include "uart.h"
#include "sampling.h"


int main(){

	sampleBufferStruct sampleBuffer;

	initUART();
	printf("Start up\n\r");
	initSampling(&sampleBuffer);

	DDRA = 0x01;
	PORTA = 0x01;

	startSampling();

	while(1){
		printf("%d;\n\r",popSample(&sampleBuffer));
	}
	return 0;
}
