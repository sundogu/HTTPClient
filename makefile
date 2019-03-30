JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Main.java \
	MyClientSocket.java \
	MyDateParser.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class