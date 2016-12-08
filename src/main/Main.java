package main;

public class Main {
	public static void main(String args[]) {
		OAEP oaep = new OAEP();

		oaep.OAEP_encode("c107782954829b34dc531c14b40e9ea482578f988b719497aa0687",
				"1e652ec152d0bfcd65190ffc604c0933d0423381");

		oaep.OAEP_decode(
				"0063b462be5e84d382c86eb6725f70e59cd12c0060f9d3778a18b7aa067f90b2178406fa1e1bf77f03f86629dd5607d11b9961707736c2d16e7c668b367890bc6ef1745396404ba7832b1cdfb0388ef601947fc0aff1fd2dcd279dabde9b10bfc51efc06d40d25f96bd0f4c5d88f32c7d33dbc20f8a528b77f0c16a7b4dcdd8f");

	}
}
