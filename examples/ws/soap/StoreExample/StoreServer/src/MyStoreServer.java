import java.util.HashMap;

public class MyStoreServer {
	
	HashMap<String, Integer> inventory = new HashMap<>();
	
	public MyStoreServer() {
		this.inventory.put("CoffeeBag", 20);
		this.inventory.put("Apple", 100);
		this.inventory.put("Banana", 50);
		this.displayContent();
	}

	public void displayContent() {
		System.out.println("----------------Store Inventory -----------------");
		System.out.println("1. CofeeBags: +" + this.inventory.get("CoffeeBag"));
		System.out.println("2. Apples: +" + this.inventory.get("Apple"));
		System.out.println("3. Bananas: +" + this.inventory.get("Banana"));
	}

	public String buyItems(String item, int quantity) {
		if (this.inventory.containsKey(item)) {
			int initialQuantity = this.inventory.get(item);
			int newQuantity;
			if (initialQuantity - quantity >= 0) {
				newQuantity = initialQuantity - quantity;
				this.inventory.remove(item);
				this.inventory.put(item,  newQuantity);
				return quantity + " " + item + " were successfully purchased!\n"
						+ ("1. CofeeBags: " + this.inventory.get("CoffeeBag") + "\n")
						+ ("2. Apples: " + this.inventory.get("Apple") + "\n")
						+ ("3. Bananas: " + this.inventory.get("Banana") + "\n")
						+ ("--------------------------------------------\n");
			}
			else {
				return "There was an error with your purchase";
			}
		}
		else {
			return "There was an error with your purchase";
		}
	}
}
