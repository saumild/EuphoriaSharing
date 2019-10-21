#!/usr/bin/python           # This is server.py file

import socket               # Import socket module


s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)         # Create a socket object
port = 5000               # Reserve a port for your service.
s.bind(("127.0.0.1", port))        # Bind to the port
print('Server is waiting for request')
s.listen(5)                 # Now wait for client connection.
while True:
   print('listening')
   c,addr = s.accept()     # Establish connection with client.
   print ('Got connection from'+addr)
   print('cmon get started')
   import cnn
   print('Done finally')
   c.send('Thank you for connecting') 
   c.close()                # Close the connection
