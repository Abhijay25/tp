---
layout: page
title: Developer Guide
---

<div class="print-toc" markdown="1">

<!-- omit in toc -->
## Table of Contents

- [**Setting up, getting started**](#setting-up-getting-started)
- [**Design**](#design)
  - [Architecture](#architecture)
  - [UI component](#ui-component)
  - [Logic component](#logic-component)
  - [Model component](#model-component)
  - [Storage component](#storage-component)
  - [Common classes](#common-classes)
  - [**Design Choices**](#design-choices)
- [**Proposed Features**](#proposed-features)
  - [\[Proposed\] Undo/redo feature](#proposed-undoredo-feature)
- [**Documentation, logging, testing, configuration, dev-ops**](#documentation-logging-testing-configuration-dev-ops)
- [**Appendix: Requirements**](#appendix-requirements)
  - [Product scope](#product-scope)
  - [User stories](#user-stories)
  - [Use cases](#use-cases)
    - [**Use Case: Add a Person**](#use-case-add-a-person)
    - [**Use Case: Delete a Person**](#use-case-delete-a-person)
    - [**Use Case: Book a Person**](#use-case-book-a-person)
    - [**Use Case: Find a Person**](#use-case-find-a-person)
    - [**Use Case: Help Menu**](#use-case-help-menu)
  - [Non-Functional Requirements](#non-functional-requirements)
  - [Glossary](#glossary)
- [**Appendix: Planned Enhancements**](#appendix-planned-enhancements)
  - [Filter Booking Table for `find`](#filter-booking-table-for-find)
  - [Reschedule Date or Time of Current Booking](#reschedule-date-or-time-of-current-booking)
  - [Edit Current Booking's Clients/Description](#edit-current-bookings-clientsdescription)
  - [Timezone Support for Scheduled Bookings](#timezone-support-for-scheduled-bookings)
- [**Appendix: Instructions for manual testing**](#appendix-instructions-for-manual-testing)
  - [Launch and shutdown](#launch-and-shutdown)
  - [Saving data](#saving-data)
  - [Error handling](#error-handling)

</div>

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

<div markdown="span" class="alert alert-primary">

:bulb: **Tip:** The `.puml` files used to create diagrams are in this document `docs/diagrams` folder. Refer to the [_PlantUML Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit diagrams.
</div>

### Architecture

<img src="images/ArchitectureDiagram.png"/>

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/FirstImpressions-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/FirstImpressions-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete n/Alex`.

<img src="images/ArchitectureSequenceDiagram.png"/>

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<img src="images/ComponentManagers.png"/>

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2526S1-CS2103T-T08-4/tp/tree/master/src/main/java/seedu/address/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/AY2526S1-CS2103T-T08-4/tp/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/AY2526S1-CS2103T-T08-4/tp//tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model` and their respective `Booking` objects.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/FirstImpressions-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete n/Alex")` API call as an example.

![Interactions Inside the Logic Component for the `delete n/Alex` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</div>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `FirstImpressionsParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<img src="images/ParserClasses.png"/>

How the parsing works:
* When called upon to parse a user command, the `FirstImpressionsParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `FirstImpressionsParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/FirstImpressions-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<img src="images/ModelClassDiagram.png"/>


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)
* A `Person` has a `Name`, `Phone`, `Email`, and may have 0 or any number of `Tag`s or `Booking`s.


<div markdown="span" class="alert alert-info">:information_source: **Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `FirstImpressions`, which `Person` references. This allows `FirstImpressions` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<img src="images/BetterModelClassDiagram.png"/>

</div>

### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/FirstImpressions-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<img src="images/StorageClassDiagramNew.png"/>

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `FirstImpressionsStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

---


### **Design Choices**

<!-- omit in toc -->
#### Find Command

<!-- omit in toc -->
##### **Aspect: Where to Perform Input Validation**
- **Alternative 1:** Validate parameters in `ClientContainsKeywordsPredicate`.
  - *Pros:* Keeps `FindCommandParser` simpler.
  - *Cons:* Predicate becomes responsible for input correctness instead of filtering logic.
- **Alternative 2 (current choice):** Validate in `FindCommandParser` before predicate creation.
  - *Pros:* Ensures only valid data reaches the model layer.
  - *Cons:* Slightly increases parser complexity.

**Chosen Approach:**
Validation is performed in `FindCommandParser` for better separation of concerns — parsing vs filtering.

<!-- omit in toc -->
##### **Aspect: Handling Multiple Prefixes**
- **Alternative 1:** Search for results using a logical **AND** operation making search results more accurate and  easy to find specific team members
- **Alternative 2 (current choice):** Search for results   using a logical **OR** operation to include as many results as possible to ensure user does not miss / mismatch any inputs and intended results
  - *Pros:* Easier to find groups of people even with mismatched input (e.g. `find n/Alex Loh` returns results for `Alex Yeoh` and `Brian Loh`)
  - *Cons:* Inability to find specific people among team members with similar names (e.g. When searching for `Alex Yeoh` with a `teamLead` tag among multiple `Alex Yeoh`s, doing `find n/Alex Yeoh t/teamLead` will list all results for both search parameters)

**Chosen Approach:**
`find` supports combining multiple prefixes using a logical **OR** relationship.
  - e.g., `find n/Alex t/friend` returns persons whose name *contains "Alex"* **or** those who have the tag *"friend"*.
- This makes it easier to find for users.

<!-- omit in toc -->
##### **Aspect: Command Format**
- **Alternative 1** Prefix before every value (current choice):
  - Example: `find t/teamLead t/friends`
  - Rationale: explicit field specifiers make tokenization deterministic, avoid ambiguity between multi-word values and separate parameters, and force deliberate searches (users must consciously mark each search term with its field).
- **Alternative 2** Implicit multiple parameters without repeated prefixes:
  - Example: `find t/ teamLead friends`
  - Rationale: more concise for users but requires heuristics to decide whether `friends` is part of the first name or a separate name; complicates tokenizer and increases chance of surprising behavior for users.

**Chosen approach:**
Prefix before every value. It trades a small amount of typing for predictable parsing, maintainable code, and fewer surprising edge cases during tokenization and validation.

--------------------------------------------------------------------------------------------------------------------

## **Proposed Features**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Undo/redo feature

<!-- omit in toc -->
#### Proposed Implementation

It extends `FirstImpressions` with an undo/redo history, stored internally as an `FirstImpressionsStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedFirstImpressions#commit()` — Saves the current address book state in its history.
* `VersionedFirstImpressions#undo()` — Restores the previous address book state from its history.
* `VersionedFirstImpressions#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitFirstImpressions()`, `Model#undoFirstImpressions()` and `Model#redoFirstImpressions()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedFirstImpressions` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

![UndoRedoState0](images/UndoRedoState0.png)

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitFirstImpressions()`, causing the modified state of the address book after the `delete n/Alex` command executes to be saved in the `FirstImpressionsStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

![UndoRedoState1](images/UndoRedoState1.png)

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitFirstImpressions()`, causing another modified address book state to be saved into the `FirstImpressionsStateList`.

![UndoRedoState2](images/UndoRedoState2.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If a command fails its execution, it will not call `Model#commitFirstImpressions()`, so the address book state will not be saved into the `FirstImpressionsStateList`.

</div>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoFirstImpressions()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

![UndoRedoState3](images/UndoRedoState3.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index 0, pointing to the initial FirstImpressions state, then there are no previous FirstImpressions states to restore. The `undo` command uses `Model#canUndoFirstImpressions()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</div>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Logic.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</div>

Similarly, how an undo operation goes through the `Model` component is shown below:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Model.png)

The `redo` command does the opposite — it calls `Model#redoFirstImpressions()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index `FirstImpressionsStateList.size() - 1`, pointing to the latest address book state, then there are no undone FirstImpressions states to restore. The `redo` command uses `Model#canRedoFirstImpressions()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</div>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitFirstImpressions()`, `Model#undoFirstImpressions()` or `Model#redoFirstImpressions()`. Thus, the `FirstImpressionsStateList` remains unchanged.

![UndoRedoState4](images/UndoRedoState4.png)

Step 6. The user executes `clear`, which calls `Model#commitFirstImpressions()`. Since the `currentStatePointer` is not pointing at the end of the `FirstImpressionsStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

![UndoRedoState5](images/UndoRedoState5.png)

The following activity diagram summarizes what happens when a user executes a new command:

<img src="images/CommitActivityDiagram.png"/>

<!-- omit in toc -->
#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* has a need to manage a significant number of contacts
* has a need to filter contacts based on certain criteria
* needs to compare contacts based on certain criteria
* needs to view and add appointments to schedules of contacts
* prefer desktop apps over other types
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: Helps leaders to keep track of their team of relationship professionals’ strengths, along with their schedules. With FirstImpressions, no client request is too hard to handle as our system is able to search through multiple preferences, ensuring the perfect match for our customers.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                                     | I want to …​                                          | So that I can…​                                                         |
| -------- | ------------------------------------------ | ---------------------------------------------------- | ---------------------------------------------------------------------- |
| `*`      | potential user                             | preview the app's features and interface             | evaluate if it meets my needs before committing to use it              |
| `* * *`  | new user                                   | see usage instructions                               | refer to instructions when I forget how to use the app                 |
| `* * *`  | new user                                   | promptly schedule clients with team members          | I can match clients and team members smoothly and efficiently          |
| `* * *`  | regular user                               | monitor team members' current booking status         | make informed decisions about workload distribution                    |
| `* *`    | long-time user                             | reschedule appointments with minimal effort          | save time when managing multiple booking changes                       |
| `* *`    | long-time user                             | edit team member details easily                      | saves effort on deleting and adding the member again                   |
| `*`      | long-time user                             | receive suggestions based on the allocations I made  | make faster decisions with personalized recommendations                |
| `* * *`  | user                                       | add new team members with their details              | maintain an up-to-date roster of available professionals               |
| `* * *`  | user                                       | delete a person                                      | remove a people when they exit the team                                |
| `* * *`  | user                                       | delete a booking                                     | remove specific bookings that have been cancelled                      |
| `* * *`  | user                                       | find a person by name                                | look up a team member's skillset and current bookings                  |
| `* * *`  | user                                       | find a person by tag                                 | quickly filter out team members with suitable skills for a client     |
| `* * *`  | user                                       | find a person by date                                | see which team members are available to take on a client               |
| `* * *`  | user                                       | see all the different available team members         | I can see the options for what I need                                  |
| `* *`    | user                                       | add descriptions to bookings                         | I can add context to assigned bookings                                 |

### Use cases

#### **Use Case: Add a Person**

**System**: FirstImpressions

**Actor**: User

<!-- omit in toc -->
##### **Main Success Scenario (MSS):**
1. User checks list of all persons
2. User requests to add specific person in the list
3. FirstImpressions adds person to the list
4. Use case ends


**Extensions**

 - 2a. Person already exists \
    FirstImpressions rejects duplicate \
    Use case ends

 - 2b. Name is too long \
    FirstImpressions throws error "Name too long" \
    Use case ends

 - 2c. Invalid name (blank/empty) \
  FirstImpressions throws error "Names should not be blank and must be 100 characters or less." \
  Use case ends

 - 2d. Too many tags \
    FirstImpressions throws error "Remove existing tag before adding new one" \
    Use case ends

 - 2e. Invalid tag \
    FirstImpressions throws error "Tag names should not be blank and should only contain letters and numbers (no spaces or special characters)." \
    Use case ends

 - 2f. Tag is too long \
    FirstImpressions throws error "Tag name is too long! Please keep it to 50 characters or less." \
    Use case ends

 - 2g. Email is too long \
    FirstImpressions throws error "Email address is too long! Please keep it to 50 characters or less." \
    Use case ends


#### **Use Case: Delete a Person**

**System**: FirstImpressions

**Actor**: User

<!-- omit in toc -->
##### **Main Success Scenario (MSS):**
1. User checks list of all persons
2. User requests to delete specific person
3. FirstImpressions deletes person in the list
4. Use case ends


**Extensions**

- 2a. Person does not exist \
  FirstImpressions throws error "Name to delete required" \
  Use case ends

- 2b. Active appointment exists \
  FirstImpressions prompts "Deleting [person]: [count] active appointment(s) exist past current date. These appointments will be automatically cancelled." \
  Use case ends


#### **Use Case: Book a Person**

**System**: FirstImpressions

**Actor**: User

<!-- omit in toc -->
##### **Main Success Scenario (MSS):**
1. User checks list of all persons
2. User requests to book client to team member at specific datetime
3. FirstImpressions adds booking to team member
4. Use case ends


**Extensions**

- 2a. Double Booking \
  FirstImpressions throws error "[Team Member] is already booked at 2025-09-18 14:00 with client [Client Name] for [other consultation]." \
  Use case ends

- 2b. Missing parameters \
  FirstImpressions throws error "Booking requires datetime, client, team member, and description." \
  Use case ends

- 2c. Invalid client name (blank/too long) \
  FirstImpressions throws error "Client name should not be blank and must be 100 characters or less." \
  Use case ends

- 2d. Invalid datetime format \
  FirstImpressions throws error "Invalid date/time format or value!\nPlease use the format: YYYY-MM-DD HH:MM (e.g., 2024-12-25 14:30)" \
  Use case ends

- 2e. Invalid datetime value \
  FirstImpressions throws error "Invalid datetime \"[formatted datetime]\", that datetime does not exist " (e.g., "Invalid datetime \"February 31st 2026 14:00\", that datetime does not exist " for invalid dates like February 31st) \
  Use case ends

- 2f. Duplicate parameter \
  FirstImpressions throws error "Parameter [parameter] specified multiple times. Each parameter should appear only once." \
  Use case ends

- 2e. Invalid Date \
  FirstImpressions throws error "Invalid date "Month [MM] [DD]th [YYYY]", that date does not exist in the (Gregorian) calendar" \
  Use case ends

- 3e. Unknown parameter \
  FirstImpressions throws error "Unknown parameter: [parameter]. Valid parameters are /d, /c, /p, /desc" \
  Use case ends


#### **Use Case: Find a Person**

**System**: FirstImpressions

**Actor**: User

<!-- omit in toc -->
##### **Main Success Scenario (MSS) - Finding by Name**

1. User requests to find persons by name using the `find n/NAME` command.
2. *FirstImpressions* parses the name parameter and validates the format.
3. *FirstImpressions* filters the contact list to show persons whose names contain the search term.
4. *FirstImpressions* displays a message indicating the number of persons found, for example:
> Searching for contacts with: <br>
> Name containing: [Input Name] <br>
> Found 3 person(s) matching your search!
5. Use case ends. <br>


**Extensions (Name search)**

- **1a.** Unknown or invalid prefix provided. \
  FirstImpressions displays an error: "Invalid command format!" \
  Use case ends.

- **3a.** No persons match the search criteria. \
  FirstImpressions displays "0 persons listed!" \
  Use case ends.

- **3b.** Valid prefix provided but no parameter (e.g., `find n/`). \
  FirstImpressions lists all persons. Use case continues as in the main scenario.

- **3c.** Partial name provided. \
  FirstImpressions lists all persons whose names contain the given substring.

<!-- omit in toc -->
##### **Main Success Scenario (MSS) - Finding by Tag**

1. User requests to find persons by tag using the `find t/TAG` command.
2. *FirstImpressions* parses the tag parameter and validates the format.
3. *FirstImpressions* filters the contact list to show persons who have the specified tag.
4. *FirstImpressions* displays a message indicating the number of persons found, for example:
> Searching for contacts with: <br>
> Tag containing: [Input Tag] <br>
> Found 2 person(s) matching your search!
5. Use case ends. <br>

**Extensions (Tag search)**

- **1a.** Unknown or invalid prefix provided. \
  FirstImpressions displays an error: "Invalid command format!" \
  Use case ends.

- **3a.** No persons match the search criteria. \
  FirstImpressions displays "0 persons listed!" \
  Use case ends.

- **3b.** Valid prefix provided but no parameter (e.g., `find t/`). \
  FirstImpressions lists all persons. Use case continues as in the main scenario.

- **3c.** Multiple valid prefixes provided. \
  FirstImpressions combines criteria (logical OR semantics) to refine results.

<!-- omit in toc -->
##### **Main Success Scenario (MSS) - Finding by Date**

1. User requests to find persons by booking date using the `find d/YYYY-MM-DD` command.
2. *FirstImpressions* parses the date parameter and validates the format.
3. *FirstImpressions* filters the contact list to show persons who have bookings on the specified date.
4. *FirstImpressions* displays a message indicating the number of persons found, for example:
> Searching for contacts with: <br>
> Booking date: [Input Date] <br>
> Found 1 person(s) matching your search!
5. Use case ends. <br>

**Extensions (Date search)**

- **1a.** Unknown or invalid prefix provided. \
  FirstImpressions displays an error: "Invalid command format!" \
  Use case ends.

- **1b.** Invalid date format provided. \
  FirstImpressions displays an error: "Invalid date! Expected format: YYYY-MM-DD (e.g., 2025-10-20)" \
  Use case ends.

- **3a.** No persons match the search criteria. \
  FirstImpressions displays "0 persons listed!" \
  Use case ends.

- **3b.** Valid prefix provided but no parameter (e.g., `find d/`). \
  FirstImpressions lists all persons. Use case continues as in the main scenario. <br>


---

#### **Use Case: Help Menu**

**System**: FirstImpressions

**Actor**: User

<!-- omit in toc -->
##### **Main Success Scenario (MSS):**
1. User requests for help menu
2. FirstImpressions shows pop-up menu with all command usage
3. Use case ends

---

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `17` or above installed.
2.  Should be able to hold up to `1000` persons without a noticeable sluggishness in performance for typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4.  Searching for 1000 contacts should return under 1s.
5.  Create, edit, delete actions should be completed under `150`ms.
6.  Should be able to import 1000 contacts from CSV in 3s or less.
7.  Should be able to export 1000 contacts to CSV or JSON in 2s or less.
8.  UI remains responsive during bulk ops.
9.  Should perform all writes atomically so that no contact data is lost on crash or power out.
10. Should autosave any contact creation, edit or delete within 1s of the action.
11. The system should support efficient keyboard navigation.
12. Should provide clear error message and guidance on failed import/export.
13. Should work for x86 and ARM processors without modification.
14. Should support multiple file types for import and export (CSV/JSON).
15. Should run offline for all core features.
16. Should log all system errors to a local file with timestamps.
17. Should not exceed 100MB in log file size.


### Glossary

* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **Team member**: A person recorded in our system
* **Team manager**: The users of FirstImpressions, who find suitable team members for clients.
* **Client**: Customers who are finding a specific person who fits certain criteria, which our team managers are finding people for.
* **Booking**: A scheduled meeting between a Client and a Team Member.

--------------------------------------------------------------------------------------------------------------------


## **Appendix: Planned Enhancements**

###  Filter Booking Table for `find`

<!-- omit in toc -->
#### Enhancement Description

Highlights relevant bookings to users searching by client name using `find d/`.

**Operations:**

  - `Model#markBookings(Predicate<Person> predicate)` — Mark all relevant bookings that match search criteria
  - `UI#highlightBookings()` — Highlights all relevant bookings

<!-- omit in toc -->
#### Usage Scenario

1.  The user uses `find` command to search for bookings under a specific client: `find d/2026-03-25` <br>
    <img src="images/FindBookingFilter.png"/> <br>

    **Booking Table Highlights Relevant Bookings**

<!-- omit in toc -->
#### Design Considerations

**Display Format:**

-  Provide clear indication to the relevant bookings by shifting it to the top of the booking list and with a highlight.
-  Only list contacts with the search criteria.
-  Ensure that all other bookings under the same contact are still present.

**Booking Storage:**

-  Ensure that the contents of the booking lists are not modified.
-  Uses a copy of the list of booking to modify the display sequence.

<!-- omit in toc -->
#### Extensions/ Error Cases

 - **Date not found:**
    Error: "No bookings with the input date found."

 - **Invalid Date**
    Error: "Invalid Date! Must be of the format YYYY-MM-DD"


---


### Reschedule Date or Time of Current Booking

<!-- omit in toc -->
#### Enhancement Description

The reschedule mechanism allows users to update the datetime of an existing booking. It interacts with the **Model** and **Booking** classes to ensure no conflicts occur.

**Operations:**

  - `Model#rescheduleBooking(Booking booking, LocalDateTime newDateTime)` — Updates the booking with a new datetime after validation.
  - `Booking#setDateTime(LocalDateTime newDateTime)` — Updates the datetime field of a booking.
  - Optional undo support: Track changes in `VersionedFirstImpressions` to allow undo/redo of reschedules.

<!-- omit in toc -->
#### Usage Scenario

1.  The user views all bookings and identifies one to reschedule (e.g., a booking for Carl Kurz).

2.  Executes the command:

    ```
     reschedule n/Carl Kurz b/2 dt/2025-10-25 14:00
    ```

    Where:

    `b/2` = booking ID

    `n/Carl Kurz` = team member name (added for clarity and verification)

    `dt/2025-10-25 14:00` = new datetime

    The Logic component parses the command and calls:

    ```
     model.rescheduleBooking(selectedBooking, newDateTime);
    ```

    The **Model** validates:

      - The booking exists (using `b/2`).
      - The team member name (`n/`) matches the team member in booking `b/2`.
      - The new datetime does not conflict with other bookings for the same team member.
      - All parameters are valid (non-null, proper format).

    If validation passes, the booking datetime is updated. Otherwise, an error is thrown (e.g., team member mismatch, conflict, or invalid date).

    The **Logic** component returns a `CommandResult` to the UI:

      - **Success Example:**

        ```
        Booking rescheduled successfully: Carl Kurz, new datetime: 2025-10-25 14:00
        ```

      - **Failure Example:** Appropriate error message. <br>

    <img src="images/RescheduleDiagram.png"/> <br>

    **Sequence Diagram for Reschedule Command**

<!-- omit in toc -->
#### Design Considerations

**Conflict Detection:**

  - Ensure the updated datetime does not conflict with other bookings for the same team member.
  - Conflicts prevent the reschedule.

**Parameter Validation:**

  - Booking ID exists.
  - Team member name (`n/`) matches the name in the booking. (New consideration)

---

### Edit Current Booking's Clients/Description

<!-- omit in toc -->
#### Enhancement Description

The edit booking mechanism allows users to update the client name or description of an existing booking without changing the datetime. This provides flexibility for booking management.

**Operations:**

  - `Model#editBooking(Booking booking, String newClientName, String newDescription)` — Updates the booking with new client name and/or description.
  - `Booking#setClientName(String newClientName)` — Updates the client name field of a booking.
  - `Booking#setDescription(String newDescription)` — Updates the description field of a booking.

<!-- omit in toc -->
#### Usage Scenario

1.  The user views all bookings and identifies one to edit (e.g., a booking for Carl Kurz).

2.  Executes the command:

    ```
     editbooking n/Carl Kurz b/2 c/Madam Wong desc/Updated consultation details
    ```

    Where:

    `b/2` = booking ID

    `n/Carl Kurz` = team member name (for verification)

    `c/Madam Wong` = new client name

    `desc/Updated consultation details` = new description

    The Logic component parses the command and calls:

    ```
     model.editBooking(selectedBooking, newClientName, newDescription);
    ```

    The **Model** validates:

      - The booking exists (using booking ID).
      - The team member name (`n/`) matches the team member in the booking.
      - At least one field (client name or description) is provided for update.
      - All parameters are valid (non-null, proper format).

    If validation passes, the booking details are updated. Otherwise, an error is thrown.

    The **Logic** component returns a `CommandResult` to the UI:

      - **Success Example:**

        ```
        Booking updated successfully: Carl Kurz, Client: Madam Wong, Description: Updated consultation details
        ```

      - **Failure Example:** Appropriate error message.

    <img src="images/EditBookingProposedSequence.png"/> <br>

    **Sequence Diagram for Edit Booking Command**

<!-- omit in toc -->
#### Design Considerations

**Field Validation:**

  - Client name must follow the same validation rules as new bookings.
  - Description must follow the same validation rules as new bookings.
  - At least one field must be provided for update.

**Atomicity:**

  - Edit booking is **all-or-nothing**: either fully applied or not applied at all.

<!-- omit in toc -->
#### Extensions / Error Cases

  - **Booking does not exist:**
    Error: "Booking ID not found"

  - **No fields to update:**
    Error: "At least one field (client name or description) must be provided for update"

  - **Invalid client name:**
    Error: "Invalid client name format"

  - **Invalid description:**
    Error: "Invalid description format"

  - **Team member mismatch:**
    Error: "Team member name does not match the booking"

---

### Timezone Support for Scheduled Bookings

<!-- omit in toc -->
#### Enhancement Description

The timezone mechanism allows users to work with bookings across different timezones, making the application suitable for global teams and clients.

**Operations:**

  - `Model#setUserTimezone(ZoneId timezone)` — Sets the user's preferred timezone.
  - `Booking#getDateTimeInTimezone(ZoneId timezone)` — Returns booking datetime converted to specified timezone.
  - `Booking#createBookingWithTimezone(String clientName, LocalDateTime datetime, String description, ZoneId timezone)` — Creates booking with timezone awareness.

<!-- omit in toc -->
#### Usage Scenario

1.  The user sets their preferred timezone: `settimezone Asia/Singapore` <br>
    <img src="images/TimeZoneSetTimeZoneSequence.png"/> <br>

    **Sequence Diagram for Setting Preferred Timezone**


2.  The user creates a booking: `book dt/2025-09-20 10:30 c/Madam Chen n/Bob Lee` <br>

    The system stores the booking in the user's timezone and can display it in other timezones when needed. <br>
    <img src="images/TimeZoneCreateBookingSequence.png"/> <br>

    **Sequence Diagram for Creating Booking with Timezone Preferences**


3.  The user views bookings in a different timezone: `settimezone timezone America/New_York` <br>

    All booking times are automatically converted and displayed in the specified timezone. <br>
    <img src="images/TimeZoneViewDiffTimezoneSequence.png"/> <br>

    **Sequence Diagram for Viewing Bookings in Different Timezone**

<!-- omit in toc -->
#### Design Considerations

**Timezone Storage:**

  - Store all booking datetimes in UTC internally.
  - Convert to user's preferred timezone for display.
  - Allow temporary timezone switching for viewing.

**User Preferences:**

  - Store user's default timezone in `UserPrefs`.
  - Allow timezone changes without affecting existing bookings.
  - Provide timezone validation and error handling.

**Display Format:**

  - Show timezone information in booking displays.
  - Provide clear indication when times are converted.
  - Support multiple timezone formats (e.g., UTC+8, Asia/Singapore).

**Data Migration:**

  - Existing bookings without timezone information default to UTC.
  - Provide migration tools for existing data.
  - Maintain backward compatibility.

<!-- omit in toc -->
#### Extensions / Error Cases

  - **Invalid timezone:**
    Error: "Invalid timezone format. Use format like 'Asia/Singapore' or 'UTC+8'"

  - **Timezone not found:**
    Error: "Timezone not recognized. Please use a valid timezone identifier"

  - **Conversion errors:**
    Error: "Unable to convert datetime to specified timezone"

  - **Missing timezone:**
    Error: "Please set your preferred timezone using 'settimezone' command"


--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Open a command terminal and navigate to the folder containing the jar file.

   1. Run `java -jar FirstImpressions.jar` <br>
      Expected: Application launches successfully with the GUI. Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   2. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

<!-- omit in toc -->
### Adding a person

1. **Adding a person with all fields**

   1. Prerequisites: List all persons using the `list` command.

   2. Test case: `add n/John Doe p/98765432 e/johndoe@example.com t/friends`<br>
      Expected: Person is added to the list. Success message shown. Person appears in the contact list.

2. **Adding a person with minimal fields**

   1. Test case: `add n/Jane Smith`<br>
      Expected: Person is added with only name. Phone and email are empty. Success message shown.

3. **Adding a duplicate person**

   1. Test case: `add n/John Doe p/98765432 e/johndoe@example.com` (assuming John Doe already exists)<br>
      Expected: Error message "This person already exists in the address book" is shown.

4. **Adding a person with invalid data**

   1. Test case: `add n/ p/123 e/invalid-email`<br>
      Expected: Error message "Names should not be blank and must be 100 characters or less."

<!-- omit in toc -->
### Editing a person

1. **Editing a person's details**

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   2. Test case: `edit n/John Doe p/91234567 e/newemail@example.com`<br>
      Expected: Person named "John Doe" has their phone and email updated. Success message shown.

2. **Editing with non-existent person**

   1. Test case: `edit n/NonExistentPerson p/91234567`<br>
      Expected: Error message "Person with name 'NonExistentPerson' not found in the address book" is shown.

3. **Editing with no fields**

   1. Test case: `edit n/John Doe`<br>
      Expected: Error message "At least one field to edit must be provided" is shown.

4. **Editing with case-sensitive name**

   1. Prerequisites: Person "John Doe" exists in the list.

   2. Test case: `edit n/john doe p/91234567`<br>
      Expected: Error message "Person with name 'john doe' not found in the address book" is shown (case-sensitive).

<!-- omit in toc -->
### Finding persons

1. **Finding by name**

   1. Prerequisites: Multiple persons in the address book.

   2. Test case: `find n/John`<br>
      Expected: All persons with "John" in their name are listed.

2. **Finding by tag**

   1. Test case: `find t/friends`<br>
      Expected: All persons with the "friends" tag are listed.

3. **Finding by date**

   1. Prerequisites: Some persons have bookings on specific dates.

   2. Test case: `find d/2025-10-20`<br>
      Expected: All persons with bookings on 2025-10-20 are listed.

4. **Finding with no results**

   1. Test case: `find n/NonExistentPerson`<br>
      Expected: "0 persons listed!" message is shown.

<!-- omit in toc -->
### Deleting a person

1. **Deleting a person while all persons are being shown**

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   2. Test case: `delete n/John Doe`<br>
      Expected: Person named "John Doe" is deleted from the list. Details of the deleted contact shown in the status message.

2. **Deleting a person that doesn't exist**

   1. Test case: `delete n/NonExistentPerson`<br>
      Expected: Error message "Person not found" is shown.

3. **Deleting with case-sensitive name**

   1. Prerequisites: Person "John Doe" exists in the list.

   2. Test case: `delete n/john doe`<br>
      Expected: Error message "Person not found" is shown (case-sensitive).

<!-- omit in toc -->
### Booking Appointments

1. **Creating a booking**

   1. Prerequisites: At least one person exists in the contact list.

   2. Test case: `book dt/2025-12-25 10:00 c/Madam Chen n/John Doe desc/Consultation`<br>
      Expected: Booking is created successfully. Success message shown. Booking appears in the person's details.

2. **Creating a booking with past date**

   1. Test case: `book dt/2020-01-01 10:00 c/Madam Chen n/John Doe`<br>
      Expected: Booking is created successfully. Success message shown with cautionary message. "Note that this is a Booking is in the past!"

3. **Creating a double booking**

   1. Prerequisites: Person "John Doe" already has a booking at 2025-12-25 10:00.

   2. Test case: `book dt/2025-12-25 10:00 c/Mr Lim n/John Doe desc/Another consultation`<br>
      Expected: Error message about double booking is shown.

4. **Creating a booking for non-existent person**

   1. Test case: `book dt/2025-12-25 10:00 c/Madam Chen n/NonExistentPerson`<br>
      Expected: Error message "Person not found" is shown.

<!-- omit in toc -->
### Clearing all entries

1. **Clearing all data**

   1. Prerequisites: Multiple persons and bookings exist in the address book.

   2. Test case: `clear f/`<br>
      Expected: All persons and bookings are removed. Success message shown. Contact list becomes empty.

### Saving data

1. **Dealing with missing data files**

   1. Close the application.

   2. Delete the `data/FirstImpressions.json` file from the application folder.

   3. Launch the application again.<br>
      Expected: Application starts with empty data. No error messages.

2. **Dealing with corrupted data files**

   1. Close the application.

   2. Open `data/FirstImpressions.json` in a text editor and corrupt the JSON format (e.g., remove a closing brace).

   3. Launch the application again.<br>
      Expected: Application starts with empty data. Error message logged but application continues to work.

3. **Data persistence**

   1. Add a person using `add n/Test Person p/12345678 e/test@example.com`.

   2. Close the application.

   3. Launch the application again.<br>
      Expected: The person "Test Person" is still in the contact list.

<!-- omit in toc -->
### Help command

1. **Accessing help**

   1. Test case: `help`<br>
      Expected: Help window opens showing available commands and User Guide link.

<!-- omit in toc -->
### Exit command

1. **Exiting the application**

   1. Test case: `exit`<br>
      Expected: Application closes immediately.

### Error handling

1. **Invalid commands**

   1. Test case: `invalidcommand`<br>
      Expected: Error message "Unknown command" is shown.

2. **Malformed commands**

   1. Test case: `add n/` (missing name)<br>
      Expected: Error message about invalid command format is shown.

3. **Commands with extra parameters**

   1. Test case: `help extraparameter`<br>
      Expected: Help window still opens (extra parameters ignored).
