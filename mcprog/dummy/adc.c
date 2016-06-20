#include "adc.h"
#include <avr/io.h>
#include <avr/interrupt.h>
#include <stdio.h>


void initADC(){
	ADMUX = (1<<REFS0) | 0x01;
	ADCSRA = (1<<ADATE) | (1<<ADPS2) | (1<<ADPS1) | (1<<ADPS0);
}

void startADC(){
	ADCSRA |= (1<<ADEN) | (1<<ADSC);
}

void stopADC(){
	ADCSRA &= ~((1<<ADEN) | (1<<ADSC) | (1<<ADIE));
}

