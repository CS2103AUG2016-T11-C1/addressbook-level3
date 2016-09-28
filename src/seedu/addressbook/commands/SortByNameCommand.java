package seedu.addressbook.commands;

import java.util.List;

import seedu.addressbook.data.person.ReadOnlyPerson;

public class SortByNameCommand extends Command {
	public static final String COMMAND_WORD = "sortbyname";// later change to
															// sort/n
	 public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n" 
	            + "Sorts all the people by name and displays the result.\n\t"
	            + "Example: " + COMMAND_WORD;
	 
	public static final String SORTED_BY_NAME = "Successfully sorted by name.";

	public CommandResult execute() {
		addressBook.sortPeopleByName();

		List<ReadOnlyPerson> allPersons = addressBook.getAllPersons().immutableListView();
		return new CommandResult(getMessageForPersonListShownSummary(allPersons), allPersons);

	}

}