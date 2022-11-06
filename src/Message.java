
abstract class Message {
	
}

class ClientMessage extends Message{
	String[] message;
	
	ClientMessage (String msg) {
		this.message = msg.trim().replaceFirst("^<","").replaceFirst(">$","").split("\\s*>*<\\s*");
	}
}
