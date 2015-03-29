package view;

import controller.MessageDisplayerController;
import controller.MulticastSocketClient;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class MessageDisplayer extends Parent {

	private TextField sheet;

	private TextArea messages;

	private Button send;
	
	private MessageDisplayerController controllerMD;
	
	private MulticastSocketClient controllerMC;
	
	/**
	 * Constructor for the socket client
	 * @param controller
	 */
	public MessageDisplayer(MessageDisplayerController controller) {
		super();
		sheet = new TextField();
		messages = new TextArea();
		send = new Button("Send");
		this.controllerMD=controller;
		controllerMC=null;
	}
	
	/**
	 * Constructor for the multicast client
	 * @param controller
	 */
	public MessageDisplayer(MulticastSocketClient controller){
		super();
		sheet = new TextField();
		messages = new TextArea();
		send = new Button("Send");
		this.controllerMC=controller;
		controllerMD=null;
	}

	public void launch() {

		BorderPane bp = new BorderPane();
		BorderPane bp2 = new BorderPane();
		
		bp.setPrefSize(800, 600);
		
		bp.setCenter(messages);
		bp.setBottom(bp2);
		
		send.setOnAction((e)-> {
			if(controllerMD!=null)
				controllerMD.writeSocket(getMessageToSend());
			else
				controllerMC.sendNetworkMessage(getMessageToSend());
			sheet.clear();
		});

		bp2.setCenter(sheet);
		bp2.setRight(send);

		this.getChildren().add(bp);
	}

	public String getMessageToSend() {
		return sheet.getText();
	}
	
	public TextField getSheet()
	{
		return sheet;
	}

	public TextArea getMessagesDisplayer() {
		return messages;
	}

	public Button getSendButton() {
		return send;
	}
}
