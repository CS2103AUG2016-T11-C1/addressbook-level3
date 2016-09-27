package seedu.addressbook.commands;

import seedu.addressbook.common.Messages;
import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.data.person.*;
import seedu.addressbook.data.tag.Tag;
import seedu.addressbook.data.tag.UniqueTagList.TagNotFoundException;

import static seedu.addressbook.ui.Gui.DISPLAYED_INDEX_OFFSET;

import java.util.ArrayList;
import java.util.Set;

/**
 * Renames a tag of a person identified using the last displayed index.
 */
public class RenameTagCommand extends Command {

    public static final String COMMAND_WORD = "renametag";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n" + "Renames a person's tag to the address book. "
            + "Parameters: INDEX [ot/OLDTAG] [nt/NEWTAG]\n\t"
            + "Example: " + COMMAND_WORD
            + " 1 ot/goodMan nt/owesMoney";

    public static final String MESSAGE_SUCCESS = "Tag renamed!";
    
    private final Person toRenameTag;
    
    /**
     * @throws IllegalValueException if the new tag name is invalid
     * @throws TagNotFoundException if the old tag is invalid
     */
    public RenameTagCommand(int index, ArrayList<String> tags) throws IllegalValueException, TagNotFoundException {
        toRenameTag = getPersonWithIndex();
    	toRenameTag.removeTag(new Tag(tags.get(0)));
        toRenameTag.addTag(new Tag(tags.get(1)));
	}

	@Override
    public CommandResult execute() {
        try {
            final ReadOnlyPerson target = getTargetPerson();
            if (!addressBook.containsPerson(target)) {
                return new CommandResult(Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK);
            }
            return new CommandResult(String.format(MESSAGE_SUCCESS));
        } catch (IndexOutOfBoundsException ie) {
            return new CommandResult(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
    }
    
    protected Person getPersonWithIndex() throws IndexOutOfBoundsException {
        return addressBook.getAllPersons().getList().get(getTargetIndex() - DISPLAYED_INDEX_OFFSET);
    }
}