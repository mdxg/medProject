#ifndef SAMPLING_H_
#define SAMPLING_H_

#include <avr/io.h>

typedef struct{
	uint16_t samples[128];
	uint8_t readPos;
	uint8_t writePos;
}sampleBufferStruct;



void initSampling(sampleBufferStruct*);
int8_t startSampling();
void stopSampling();

uint16_t popSample(sampleBufferStruct*);
void pushSample(sampleBufferStruct*, uint16_t);

#endif
