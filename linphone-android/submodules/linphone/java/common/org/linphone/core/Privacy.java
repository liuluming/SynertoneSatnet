package org.linphone.core;

public interface Privacy {
	int NONE=0;
	int USER=0x1;
	int HEADER=0x2;
	int SESSION=0x4;
	int ID=0x8;
	int CRITICAL=0x10;
	int DEFAULT=0x8000;
}
