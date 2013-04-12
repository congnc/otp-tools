
Compiling: 

    javac src/*.java -d ./

If STUBS files needed:

    rmic otp.network.OTPServer

Documentation:

    javadoc src/*.java -d ./doc -author -version

3 tools:

MyOTPPassword:

Create OTP passwords and save them into a file called 'users.db'.
Need a user name as first parameter of the command.

MyOTPServeur:

Network server which manages OTP authentication.

MyOTPClient:

Authentication a client with OTP to the server.


Implementation

RFCs 1760, 1938, 2289.
