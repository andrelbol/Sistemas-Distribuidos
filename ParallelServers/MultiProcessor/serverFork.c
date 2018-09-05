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

#define MYPORT 3490 /* the port users connect to */
#define BACKLOG 10  /* max no. of pending connections in server queue */
#define MAXDATASIZE 200

void sigchld_handler(int s)
{
  while (wait(NULL) > 0)
    ; /* wait for any child to finish */
}

void assertFunction(int functionReturn, char *message){
  if(functionReturn == -1){
    perror(message);
    exit(1);
  }
}

int main(void)
{
  int listenfd;                   /* listening socket */
  int connfd;                     /* connection socket */
  struct sockaddr_in server_addr; /* info for my addr i.e. server */
  struct sockaddr_in client_addr; /* client's address info */
  int sin_size;                   /* size of address structure */
  struct sigaction sa;            /* deal with signals from dying children! */
  int yes = 1;
  char clientAddr[20]; /* holds ascii dotted quad address */
  assertFunction(listenfd = socket(AF_INET, SOCK_STREAM, 0), "Server socket");

  /* Set Unix socket level to allow address reuse */
  assertFunction(setsockopt(listenfd, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(int)), "Server setsockopt");
  sin_size = sizeof(server_addr);
  memset(&server_addr, 0, sin_size);        /* zero struct */
  server_addr.sin_family = AF_INET;         /* host byte order ... */
  server_addr.sin_port = htons(MYPORT);     /* . short, network byte order */
  server_addr.sin_addr.s_addr = INADDR_ANY; /* any server IP addr */
  assertFunction(bind(listenfd, (struct sockaddr *)&server_addr, sizeof(struct sockaddr)), "Server bind");
  assertFunction(listen(listenfd, BACKLOG), "Server listen");
  /* Signal handler stuff */
  sa.sa_handler = sigchld_handler; /* reap all dead processes */
  sigemptyset(&sa.sa_mask);
  sa.sa_flags = SA_RESTART;
  assertFunction(sigaction(SIGCHLD, &sa, NULL), "Server sigaction");
  while (1)
  { /* main accept() loop */
    sin_size = sizeof(struct sockaddr_in);
    assertFunction(connfd = accept(listenfd, (struct sockaddr *)&client_addr, &sin_size), "Server accept");
    strcpy(clientAddr, inet_ntoa(client_addr.sin_addr));
    printf("Server: got connection from %s\n", clientAddr);
    if (!fork())
    { /* the child process dealing with a client */
      char msg[MAXDATASIZE];
      int numbytes;
      close(listenfd); /* child does not need the listener */
      msg[0] = '\0';   /* no message yet! */
      do
      {
        assertFunction(numbytes = recv(connfd, msg, MAXDATASIZE - 1, 0), "Server recv");
        msg[numbytes] = '\0'; /* end of string */
        fprintf(stderr, "Message received: %s\n", msg);
        assertFunction(send(connfd, "Hello, World!\n", 14, 0), "Server send");
      } while (strcmp(msg, "quit") != 0);
      close(connfd);
      exit(0);     /* end of child! */
    }              /* fork() */
    close(connfd); /* parent does not need the connection socket */
  }                /* while( 1) */
  return 0;
}