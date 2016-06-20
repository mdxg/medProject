#include "sampling.h"
#include <avr/interrupt.h>
#include <stdio.h>

static sampleBufferStruct *__sampleBuffer;
static uint32_t __sampleSum = 0;
static uint16_t __sampleSumNumber = 0;

void initSampling(sampleBufferStruct *buffer){
	buffer->readPos = 0;
	buffer->writePos = 0;
	__sampleBuffer = buffer;

	//ADC init
	ADMUX = (1<<REFS0) | 0x01;										//read chanel 1 with AVCC as ref
	ADCSRA = (1<<ADATE) | (1<<ADPS2) | (1<<ADPS1) | (1<<ADPS0);		//ADC clock = F_CPU / 128 auto triggered(self)

	//Timer init
	TCCR1A = 0x00;
	TCNT1 = 0x00;
	TCCR1B = (1<<WGM12);
	OCR1A = 2048;
	TIMSK = 0x00;
}

int8_t startSampling(){

	//static values init
	__sampleSum = 0;
	__sampleSumNumber = 0;
	__sampleBuffer->readPos = 0;
	__sampleBuffer->writePos = 0;

	//ADC start
	ADCSRA |= (1<<ADEN) | (1<<ADSC);

	//Timer start
	TCCR1B |= (1<<CS10);
	TIMSK |= (1<<OCIE1A);
	SREG |= (1<<7);

	return 0;
}

void stopSampling(){
	//Stop ADC
	ADCSRA &= ~((1<<ADEN) | (1<<ADSC) | (1<<ADIE));

	//Stop Timer
	TCCR1B &= ~(1<<CS10);
	TIMSK &= ~(1<<OCIE1A);
}

uint16_t popSample(sampleBufferStruct *buffer){
	uint16_t data;
	//Wait till new samples are in
	while(buffer->readPos == buffer->writePos);

	data = buffer->samples[buffer->readPos];
	buffer->readPos = (buffer->readPos + 1) % 128;

	return data;
}

void pushSample(sampleBufferStruct *buffer, uint16_t data){
	buffer->samples[buffer->writePos] = data;
	buffer->writePos = (buffer->writePos + 1) % 128;
}

ISR(TIMER1_COMPA_vect){
	uint16_t value;
	__sampleSumNumber++;
	__sampleSum += ADC;
	if(__sampleSumNumber == 900){
		value = __sampleSum / (uint32_t)900;
		pushSample(__sampleBuffer, value);
		__sampleSumNumber = 0;
		__sampleSum = 0;
	}
}
