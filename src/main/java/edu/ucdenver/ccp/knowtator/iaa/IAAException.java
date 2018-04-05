package edu.ucdenver.ccp.knowtator.iaa;

public class IAAException extends Exception {

//	public IAAException() {
//		super();
//	}

	IAAException(String message) {
		super(message);
	}

//	public IAAException(String message, Throwable cause) {
//		super(message, cause);
//	}

	IAAException(Throwable cause) {
		super(cause);
	}

}
