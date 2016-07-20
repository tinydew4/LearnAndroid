#include <stdio.h>
#include <unistd.h>

int main(int argc, char *argv[])
{
	execl("ls", "ls", (char*)0);

	return 0;
}

