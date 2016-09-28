package seedu.addressbook.parser;

import seedu.addressbook.commands.*;
import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.data.tag.UniqueTagList.TagNotFoundException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static seedu.addressbook.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

/**
 * Parses user input.
 */
public class Parser {

	public static final Pattern PERSON_INDEX_ARGS_FORMAT = Pattern.compile("(?<targetIndex>.+)");

	public static final Pattern KEYWORDS_ARGS_FORMAT = Pattern.compile("(?<keywords>\\S+(?:\\s+\\S+)*)"); // one
																											// or
																											// more
																											// keywords
																											// separated
																											// by
																											// whitespace

	public static final Pattern PERSON_DATA_ARGS_FORMAT = // '/' forward slashes
															// are reserved for
															// delimiter
															// prefixes
			Pattern.compile("(?<name>[^/]+)" + " (?<isPhonePrivate>p?)p/(?<phone>[^/]+)"
					+ " (?<isEmailPrivate>p?)e/(?<email>[^/]+)" + " (?<isAddressPrivate>p?)a/(?<address>[^/]+)"
					+ "(?<tagArguments>(?: t/[^/]+)*)"); // variable number of
															// tags

	/**
	 * Signals that the user input could not be parsed.
	 */
	public static class ParseException extends Exception {
		ParseException(String message) {
			super(message);
		}
	}

	/**
	 * Used for initial separation of command word and args.
	 */
	public static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

	public Parser() {
	}

	/**
	 * Parses user input into command for execution.
	 *
	 * @param userInput
	 *            full user input string
	 * @return the command based on the user input
	 * @throws TagNotFoundException
	 * @throws IllegalValueException
	 */
	public Command parseCommand(String userInput) throws IllegalValueException, TagNotFoundException {
		final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
		if (!matcher.matches()) {
			return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
		}

		final String commandWord = matcher.group("commandWord");
		final String arguments = matcher.group("arguments");
		switch (commandWord) {

		case AddCommand.COMMAND_WORD:
			return prepareAdd(arguments);

		case EditCommand.COMMAND_WORD:
			return prepareEdit(arguments);

		case DeleteCommand.COMMAND_WORD:
			return prepareDelete(arguments);

		case ClearCommand.COMMAND_WORD:
			return new ClearCommand();

		case FindCommand.COMMAND_WORD:
			return prepareFind(arguments);

		case FindTagCommand.COMMAND_WORD:
			return prepareFindTag(arguments);

		case ListCommand.COMMAND_WORD:
			return new ListCommand();

		case RenameTagCommand.COMMAND_WORD:
			return prepareRenameTag(arguments);

		case ViewCommand.COMMAND_WORD:
			return prepareView(arguments);

		case ViewAllCommand.COMMAND_WORD:
			return prepareViewAll(arguments);
			
		case SortByNameCommand.COMMAND_WORD:
        	return new SortByNameCommand();

		case ExitCommand.COMMAND_WORD:
			return new ExitCommand();

		case HelpCommand.COMMAND_WORD: // Fallthrough
		default:
			return new HelpCommand();
		}
	}

	/**
	 * Parses arguments in the context of the add person command.
	 *
	 * @param args
	 *            full command args string
	 * @return the prepared command
	 */
	private Command prepareAdd(String args) {
		final Matcher matcher = PERSON_DATA_ARGS_FORMAT.matcher(args.trim());
		// Validate arg string format
		if (!matcher.matches()) {
			return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
		}
		try {
			return new AddCommand(matcher.group("name"),

					matcher.group("phone"), isPrivatePrefixPresent(matcher.group("isPhonePrivate")),

					matcher.group("email"), isPrivatePrefixPresent(matcher.group("isEmailPrivate")),

					matcher.group("address"), isPrivatePrefixPresent(matcher.group("isAddressPrivate")),

					getTagsFromArgs(matcher.group("tagArguments")));
		} catch (IllegalValueException ive) {
			return new IncorrectCommand(ive.getMessage());
		}
	}

	/**
	 * Parses arguments in the context of the edit person command.
	 *
	 * @param args
	 *            full command args string
	 * @return the prepared command
	 */
	private Command prepareEdit(String args) {
		final Matcher matcher = PERSON_DATA_ARGS_FORMAT.matcher(args.trim());
		// Validate arg string format
		if (!matcher.matches()) {
			return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
		}
		try {
			return new EditCommand(getIndex(args),

					removeFirstWord(matcher.group("name")),

					matcher.group("phone"), isPrivatePrefixPresent(matcher.group("isPhonePrivate")),

					matcher.group("email"), isPrivatePrefixPresent(matcher.group("isEmailPrivate")),

					matcher.group("address"), isPrivatePrefixPresent(matcher.group("isAddressPrivate")));
		} catch (IllegalValueException ive) {
			return new IncorrectCommand(ive.getMessage());
		}
	}

	private int getIndex(String args) {
		args = args.trim();
		String value = args.substring(0, args.indexOf(" "));
		return Integer.parseInt(value);
	}

	private String removeFirstWord(String args) {
		args = args.trim();
		String value = args.substring(args.indexOf(" ") + 1);
		return value;
	}

	/**
	 * Checks whether the private prefix of a contact detail in the add
	 * command's arguments string is present.
	 */
	private static boolean isPrivatePrefixPresent(String matchedPrefix) {
		return matchedPrefix.equals("p");
	}

	/**
	 * Extracts the new person's tags from the add command's tag arguments
	 * string. Merges duplicate tag strings.
	 */
	private static Set<String> getTagsFromArgs(String tagArguments) throws IllegalValueException {
		// no tags
		if (tagArguments.isEmpty()) {
			return Collections.emptySet();
		}
		// replace first delimiter prefix, then split
		final Collection<String> tagStrings = Arrays.asList(tagArguments.replaceFirst(" t/", "").split(" t/"));
		return new HashSet<>(tagStrings);
	}

	/**
	 * Parses arguments in the context of the delete person command.
	 *
	 * @param args
	 *            full command args string
	 * @return the prepared command
	 */
	private Command prepareDelete(String args) {
		try {
			final int targetIndex = parseArgsAsDisplayedIndex(args);
			return new DeleteCommand(targetIndex);
		} catch (ParseException | NumberFormatException e) {
			return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
		}
	}

	/**
	 * Parses arguments in the context of the view command.
	 *
	 * @param args
	 *            full command args string
	 * @return the prepared command
	 */
	private Command prepareView(String args) {

		try {
			final int targetIndex = parseArgsAsDisplayedIndex(args);
			return new ViewCommand(targetIndex);
		} catch (ParseException | NumberFormatException e) {
			return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewCommand.MESSAGE_USAGE));
		}
	}

	/**
	 * Parses arguments in the context of the view all command.
	 *
	 * @param args
	 *            full command args string
	 * @return the prepared command
	 */
	private Command prepareViewAll(String args) {

		try {
			final int targetIndex = parseArgsAsDisplayedIndex(args);
			return new ViewAllCommand(targetIndex);
		} catch (ParseException | NumberFormatException e) {
			return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewAllCommand.MESSAGE_USAGE));
		}
	}

	/**
	 * Parses the given arguments string as a single index number.
	 *
	 * @param args
	 *            arguments string to parse as index number
	 * @return the parsed index number
	 * @throws ParseException
	 *             if no region of the args string could be found for the index
	 * @throws NumberFormatException
	 *             the args string region is not a valid number
	 */
	private int parseArgsAsDisplayedIndex(String args) throws ParseException, NumberFormatException {
		final Matcher matcher = PERSON_INDEX_ARGS_FORMAT.matcher(args.trim());
		if (!matcher.matches()) {
			throw new ParseException("Could not find index number to parse");
		}
		return Integer.parseInt(matcher.group("targetIndex"));
	}

	/**
	 * Parses arguments in the context of the find person command.
	 *
	 * @param args
	 *            full command args string
	 * @return the prepared command
	 */
	private Command prepareFind(String args) {
		final Matcher matcher = KEYWORDS_ARGS_FORMAT.matcher(args.trim());
		if (!matcher.matches()) {
			return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
		}

		// keywords delimited by whitespace
		final String[] keywords = matcher.group("keywords").split("\\s+");
		final Set<String> keywordSet = new HashSet<>(Arrays.asList(keywords));
		return new FindCommand(keywordSet);
	}

	private Command prepareFindTag(String args) {
		final Matcher matcher = KEYWORDS_ARGS_FORMAT.matcher(args.trim());
		if (!matcher.matches()) {
			return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
		}

		// keywords delimited by whitespace
		final String[] keywords = matcher.group("keywords").split("\\s+");
		final Set<String> keywordSet = new HashSet<>(Arrays.asList(keywords));
		return new FindTagCommand(keywordSet);
	}

	private Command prepareRenameTag(String args) throws IllegalValueException, TagNotFoundException {
		final Matcher matcher = PERSON_DATA_ARGS_FORMAT.matcher(args.trim());

		return new RenameTagCommand(getIndex(args), getTagsForRenaming(matcher.group("tagArguments")));
	}

	private ArrayList<String> getTagsForRenaming(String tagArguments) {
		final ArrayList<String> tags = (ArrayList<String>) Arrays
				.asList(tagArguments.replaceFirst(" ot/", "").split(" nt/"));
		return tags;
	}

}