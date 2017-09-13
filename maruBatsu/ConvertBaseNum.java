package maruBatsu;

/**
 * 基数変換クラス
 * @author proglight
 */

public class ConvertBaseNum {

	//n( < 10)進数を10進数へ変換
	int convertTo10(int num, int base){
		int converted = 0;
		int value;
		int i = 0;

		while(num > 0){
			value = num % 10;
			num = num / 10;
			converted += value * Math.pow(base, i);
			i++;
		}

		return converted;
	}

	//10進数をn( < 10)進数へ変換
	int convertToN(int num, int base){
		int converted = 0;
		int[] value = new int[999];
		int i = 0, length;

		while(num > 0){
			value[i] = num % base;
			num = num / base;
			i++;
		}
		length = i;
		for(i=0; i<length; i++){
			converted *= 10;
			converted += value[length-i-1];
		}

		return converted;
	}
}
