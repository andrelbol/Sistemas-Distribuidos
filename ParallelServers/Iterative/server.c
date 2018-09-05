#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/wait.h>
#include <signal.h>

#define MYPORT 3490
#define MAX_PENDING_CONNECTIONS 10 /* max no. of pending connections in server queue */
#define MAXDATASIZE 200

void sigchld_handler(int s)
{
  while (wait(NULL) > 0)
    ; /* wait for any child to finish */
}
void assertFunction(int functionReturn, char *message)
{
  if (functionReturn == -1)
  {
    perror(message);
    exit(1);
  }
}

void main(int argc, char const *argv[])
{
  int listeningSocket, connectionSocket, addressSize;
  struct sockaddr_in serverAddress, clientAddress;
  struct sigaction sa;
  int yes = 1;
  char clientAddressASCII[20];

  assertFunction(listeningSocket = socket(AF_INET, SOCK_STREAM, 0), "Server socket");
  assertFunction(setsockopt(listeningSocket, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(int)), "Server setsockopt");
  addressSize = sizeof(serverAddress);
  memset(&serverAddress, 0, addressSize);
  serverAddress.sin_family = AF_INET;         /* host byte order ... */
  serverAddress.sin_port = htons(MYPORT);     /* . short, network byte order */
  serverAddress.sin_addr.s_addr = INADDR_ANY; /* any server IP addr */
  assertFunction(bind(listeningSocket, (struct sockaddr *)&serverAddress, sizeof(struct sockaddr)), "Server bind");

  sa.sa_handler = sigchld_handler; /* reap all dead processes */
  sigemptyset(&sa.sa_mask);
  sa.sa_flags = SA_RESTART;
  assertFunction(sigaction(SIGCHLD, &sa, NULL), "Server sigaction");
  assertFunction(listen(listeningSocket, MAX_PENDING_CONNECTIONS), "Server listen");  
  while (1)
  {
    addressSize = sizeof(struct sockaddr_in);
    assertFunction(connectionSocket = accept(listeningSocket, (struct sockaddr *)&clientAddress, &addressSize), "Server accept");
    strcpy(clientAddressASCII, inet_ntoa(clientAddress.sin_addr));
    printf("Server: got connection from %s\n", clientAddressASCII);
    char msg[MAXDATASIZE];
    int numbytes;
    msg[0] = '\0';
    do
    {
      assertFunction(numbytes = recv(connectionSocket, msg, MAXDATASIZE - 1, 0), "Server recv");
      msg[numbytes] = '\0'; /* end of string */
      fprintf(stderr, "Message received: %s\n", msg);
      assertFunction(send(connectionSocket, "Hello, World!\n", 14, 0), "Server send");
    } while (strcmp(msg, "quit") != 0);
  }
}
