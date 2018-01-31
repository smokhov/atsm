import java.util.HashMap;

public class MyStoreServer {
	
	HashMap<String, Integer> oInventory = new HashMap<>();
	
	public MyStoreServer()
	{
		this.oInventory.put("CoffeeBag", 20);
		this.oInventory.put("Apple", 100);
		this.oInventory.put("Banana", 50);
		this.displayContent();
	}

	public void displayContent()
	{
		System.out.println("----------------Store inventory -----------------");
		System.out.println("1. CofeeBags: +" + this.oInventory.get("CoffeeBag"));
		System.out.println("2. Apples: +" + this.oInventory.get("Apple"));
		System.out.println("3. Bananas: +" + this.oInventory.get("Banana"));
	}

	public String buyItems(String strItem, int iQuantity)
	{
		if (this.oInventory.containsKey(strItem))
		{
			int _iInitialQuantity = this.oInventory.get(strItem);
			int _iNewQuantity;
			if (_iInitialQuantity - iQuantity >= 0)
			{
				_iNewQuantity = _iInitialQuantity - iQuantity;
				this.oInventory.remove(strItem);
				this.oInventory.put(strItem,  _iNewQuantity);
				return iQuantity + " " + strItem + " were successfully purchased!\n"
						+ ("1. CofeeBags: " + this.oInventory.get("CoffeeBag") + "\n")
						+ ("2. Apples: " + this.oInventory.get("Apple") + "\n")
						+ ("3. Bananas: " + this.oInventory.get("Banana") + "\n")
						+ ("--------------------------------------------\n");
			}
			else
			{
				return "There was an error with your purchase";
			}
		}
		else 
		{
			return "There was an error with your purchase";
		}
	}
}
