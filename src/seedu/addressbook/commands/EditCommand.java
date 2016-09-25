package seedu.addressbook.commands;


import static seedu.addressbook.ui.Gui.DISPLAYED_INDEX_OFFSET;

import java.util.Arrays;

import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.data.person.Address;
import seedu.addressbook.data.person.Email;
import seedu.addressbook.data.person.Name;
import seedu.addressbook.data.person.Person;
import seedu.addressbook.data.person.Phone;
import seedu.addressbook.data.person.ReadOnlyPerson;
import seedu.addressbook.data.person.UniquePersonList;
import seedu.addressbook.data.tag.UniqueTagList;

/**
 * Terminates the program.
 */
public class EditCommand extends Command {

	public static final String COMMAND_WORD = "edit";
	public static final String MESSAGE_USAGE = COMMAND_WORD + ": \nEdits the name, phone, address or email of a specified person.\n"
			+ "Example: " + COMMAND_WORD
			+ " 1 John Doe p/98765432 e/johnd@gmail.com a/311, Clementi Ave 2, #02-25 t/friends t/owesMoney";
	public static final String MESSAGE_SUCCESS = "Edited Successfully!";
	public static final String MESSAGE_FAIL = "Editing failed!";

	private final Person personWithNewValues;

	/**
	 * Convenience constructor using raw values.
	 *
	 * @throws IllegalValueException
	 *             if any of the raw values are invalid
	 */
	public EditCommand(int person_index, String name, String phone, boolean isPhonePrivate, String email,
			boolean isEmailPrivate, String address, boolean isAddressPrivate)
			throws IllegalValueException {
		super(person_index);
		this.personWithNewValues = new Person(new Name(name), new Phone(phone, isPhonePrivate), new Email(email, isEmailPrivate),
			new Address(address, isAddressPrivate), new UniqueTagList());
	}

	@Override
	public CommandResult execute() {
		return updateInAddressBook(getPersonWithIndex()) ? new CommandResult(MESSAGE_SUCCESS) : new CommandResult(MESSAGE_FAIL);
	}
	
	/**
     * Extracts the the target person in the last shown list from the given arguments.
     *
     * @throws IndexOutOfBoundsException if the target index is out of bounds of the last viewed listing
     */
    protected Person getPersonWithIndex() throws IndexOutOfBoundsException {
        return addressBook.getAllPersons().getList().get(getTargetIndex() - DISPLAYED_INDEX_OFFSET);
    }

	private boolean updateInAddressBook(Person person) {
		person.setName(personWithNewValues.getName());
		person.setPhone(personWithNewValues.getPhone());
		person.setEmail(personWithNewValues.getEmail());
		person.setAddress(personWithNewValues.getAddress());
		return true;
	}

}
