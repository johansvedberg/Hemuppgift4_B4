package main;

public class Main {
	public static void main(String args[]) {
		OAEP oaep = new OAEP();

		oaep.OAEP_encode("c107782954829b34dc531c14b40e9ea482578f988b719497aa0687");

		oaep.OAEP_decode(null);

	}
}
