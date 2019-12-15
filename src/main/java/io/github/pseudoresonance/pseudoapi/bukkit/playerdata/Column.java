package io.github.pseudoresonance.pseudoapi.bukkit.playerdata;

public class Column {

	private final String name;
	private final String type;
	private final String defaultValue;

	/**
	 * Constructs a new {@link Column} with the given paramters
	 * 
	 * @param name Column name
	 * @param type Column data type
	 * @param defaultValue Default column value
	 */
	public Column(String name, String type, String defaultValue) {
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
	}

	/**
	 * Constructs a new {@link Column} with the given paramters
	 * 
	 * @param name Column name
	 * @param type Column data type
	 */
	public Column(String name, String type) {
		this.name = name;
		this.type = type;
		this.defaultValue = "NULL";
	}

	/**
	 * Returns column name
	 * 
	 * @return Column name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns column data type
	 * 
	 * @return Column data type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Returns default column value
	 * 
	 * @return Default column value
	 */
	public String getDefaultValue() {
		return this.defaultValue;
	}

	/**
	 * Returns whether or not two {@link Column} objects are equal
	 * 
	 * @return Whether or not two {@link Column} objects are equal
	 */
	public boolean equals(Object obj) {
		if (obj instanceof Column) {
			Column col = (Column) obj;
			if (col.getName().equalsIgnoreCase(this.name) && col.getType().equalsIgnoreCase(this.type) && col.getDefaultValue().equals(this.defaultValue))
				return true;
		}
		return false;
	}

}
