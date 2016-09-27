package seedu.addressbook.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.addressbook.data.person.ReadOnlyPerson;
import seedu.addressbook.data.tag.Tag;

public class FindTagCommand extends Command{
	
	public static final String COMMAND_WORD = "findtag";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n" + "Finds all persons whose names contain any of "
            + "the specified tags (case-sensitive) and displays them as a list with index numbers.\n\t"
            + "Parameters: TAG [MORE_TAGS]...\n\t"
            + "Example: " + COMMAND_WORD + " school work family";

    private final Set<String> tags;

    public FindTagCommand(Set<String> tags) {
        this.tags = tags;
    }
    
    public Set<String> getTags() {
        return tags;
    }

	@Override
	public CommandResult execute() {
		final List<ReadOnlyPerson> personsFound = getPersonsWithAnyTag(tags);
		return new CommandResult(getMessageForPersonListShownSummary(personsFound), personsFound);
	}
	
    private List<ReadOnlyPerson> getPersonsWithAnyTag(Set<String> tags) {
        final List<ReadOnlyPerson> matchedPersons = new ArrayList<>();
        for (ReadOnlyPerson person : addressBook.getAllPersons()) {
            final Set<Tag> personTags = person.getTags().toSet();
            final Set<String> personStringTags = new HashSet<String>();
            for(Tag tag: personTags){
            	personStringTags.add(tag.toString().substring(1, tag.toString().length()-1));
            }
            if (!Collections.disjoint(personStringTags, tags)) {
                matchedPersons.add(person);
            }
        }
        return matchedPersons;
    }

}
