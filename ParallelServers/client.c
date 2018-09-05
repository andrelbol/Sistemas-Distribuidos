/* msgclient.c
* needs to be supplied with host name or IP address of server
* e.g. msgclient localhost or msgclient 127.0.0.1.
* simply receives a message from the server then dies!
*/
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>      /* for gethostbyname() */
#define PORT 3490       /* server port the client connects to */
#define MAXDATASIZE 100 /* max bytes to be received at once */

void assertFunction(int functionReturn, char *message)
{
  if (functionReturn == -1)
  {
    perror(message);
    exit(1);
  }
}

int main(int argc, char *argv[])
{
  int sockfd, numbytes;
  char buf[MAXDATASIZE];
  struct hostent *he;
  struct sockaddr_in their_addr; /* server address info */
  char msg[MAXDATASIZE];
  if (argc != 2)
  {
    fprintf(stderr, "usage: client hostname\n");
    exit(1);
  }
  /* resolve server host name or IP address */
  if ((he = gethostbyname(argv[1])) == NULL)
  { /* host server info */
    perror("Client gethostbyname");
    exit(1);
  }
  assertFunction(sockfd = socket(AF_INET, SOCK_STREAM, 0), "Client socket");
  memset(&their_addr, 0, sizeof(their_addr)); /* zero all */
  their_addr.sin_family = AF_INET;            /* host byte order .. */
  their_addr.sin_port = htons(PORT);          /* .. short, network byte order */
  their_addr.sin_addr = *((struct in_addr *)he->h_addr);
  assertFunction(connect(sockfd, (struct sockaddr *)&their_addr, sizeof(struct sockaddr)), "Client connect");
  do
  {
    printf("Message to send: ");
    scanf("%s", msg);
    assertFunction(numbytes = send(sockfd, msg, strlen(msg), 0), "Client send");
    assertFunction(numbytes = recv(sockfd, buf, MAXDATASIZE - 1, 0), "Client recv");
    buf[numbytes] = '\0'; /* end of string char */
    printf("Received: %s\n", buf);
  } while (strcmp(msg, "quit") != 0);
  close(sockfd);
  return 0;
}