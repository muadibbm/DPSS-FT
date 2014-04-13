package replicaB;

class UserPlayer {
	
	String IP;
	String firstName;
	String lastName;
	String userName;
	String password;
	int age;
	boolean status;
	
	
	protected UserPlayer(String FirstName, String LastName,int Age, String Username, String Password, String IPAddress){
		
		this.IP = IPAddress;
		this.age = Age;
		this.firstName = FirstName;
		this.lastName = LastName;
		this.userName = Username;
		this.password = Password;
		this.status = false;
	}
	
	/**
	 * Get First Name
	 * @return
	 */	
	String getFirstName () {
		return firstName;
	}
	
	/**
	 * Get Last Name
	 * @return
	 */	
	String getLastName () {
		return lastName;
	}
	
	/**
	 * Get Age
	 * @return
	 */
	int getAge () {
		return age;
	}
	/**
	 * Return the account username
	 * @return
	 */
	String getUserName () {
		return userName;
	}
	/**
	 * return the account password
	 * @return
	 */
	String getPassword () {
		return password;
	}

	/**
	 * Return the user status
	 * true if the user is logged in, false if not
	 * @return
	 */
	Boolean getStatus(){
		
		if ( status==true ) {
			return true;
		}
		else {
			return false;
		}
			
	}
	/**
	 * Simply switch the user status if it is true change to false and backwards
	 */
	void changeStatus () {
		this.status = !this.status;
	}
}
