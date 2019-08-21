// TODO: Declare a class called Position that takes in two fields: row and col
class Position {
    constructor(row, column) {
        this.row = row;
        this.col = column;
    }
}



// INPUT: The Position of the previously clicked tile and the Position of the current tile
// OUTPUT: boolean, true if the currPosition is a valid neighbor of lastPosition, including if
//         currPosition is the same as lastPosition; false otherwise
function isValidClick(lastPosition, currPosition) {
    
    // TODO: Fill this out!
    const rdiff = Math.abs(lastPosition.row - currPosition.row);
    const cdiff = Math.abs(lastPosition.col - currPosition.col);
    if (cdiff === 0 && rdiff === 0) {
        return true;
    } else if (rdiff === 1 && cdiff === 0) {
        return true;
    } else if (rdiff === 0 && cdiff === 1) {
        return true;
    } else if (rdiff === 1 && cdiff === 1) {
        return true;
    } else {
        return false;
    }

}

// Helper function for parsing coordinates from id
function getCoordinateArray(id) {
    return id.split('-').map(x => parseInt(x));
}

// Variables to keep track of game state
let currWord = '',
  totalScore = 0,
  guessList = [],
  positions = [];

// Waits for DOM to load before running
$(document).ready(() => {

    // TODO: Use jQuery to get reference to the HTML element with an id of "score".
    // We'll use this later to display the score.
    const $scoreText = $("#score");

    // TODO: use jQuery to get reference to the HTML element with an id of "message".
    // We'll use this later to inform the player whether the guess was correct.
    const $message = $("#message");

    // TODO: Use jQuery to get reference to the HTML <ul> element with an id of "guesses".
    // We'll append guesses to this element to display them to the user.
    const $guesses = $("#guesses");

    // Paint on the canvas on each click.
    $('#board').click(paintOnClick);

    // Listen for keypress events. If player presses the Enter key, the game validates the current word.
    $(document).keypress(event => {
        // 13 is the key code for the Enter key
        if (event.which == 13 && currWord.length > 0) {
            if (!guessList.includes(currWord)) {
                // Adds currWord to guessList array
                guessList.push(currWord);

                // Update the guesses input element of our hidden form
                $('input[name=guesses]').val(guessList.join(" "));

                // TODO: Use jQuery .append() to append currWord to $guesses. This should update
                //       the DOM by adding a new list item.
                const txt = document.createElement("li");
                txt.innerHTML = currWord;
                $guesses.append(txt);

                // HINT: You'll need to create string that wraps currWord in "li" (list item) tags!
                //       Check online for examples of jQuery's .append() function and documentation.

                // TODO: Build the Javascript object that contains data for the POST request.
                const postParameters = {"word": currWord};

                // TODO: Make a POST request to the "/validate" endpoint with the word information
                $.post("/validate", postParameters, responseJSON => {

                    // TODO: Parse the JSON response into a JavaScript object.
                    const responseObject = JSON.parse(responseJSON);
                    
                    // TODO: Fill in the following conditionals for updating score if the word was valid.
                    // If the word was valid....
                    if (responseObject.isValid) {

                        // TODO:
                        // 1. Update totalScore
                        totalScore += responseObject.score;
                        // 2. Update the text in $scoreText with the updated score
                        $scoreText.html(totalScore);
                        // 3. Update the text in $message with a happy message :D
                        $message.html("You got one!");

                        // HINT: Check out jQuery's .html() function to update the text! Again, there's
                        //       lots of examples and documentation online.

                    } else { // If the word was not valid...

                        // TODO: Update the text in $message with a sad message :(
                        $message.html("Not valid - try again!");

                    }
                });
            }

            // Select all elements with the "selected" class and remove the "selected" class
            $('.selected').css("background-color", 
                "white");
            $('.selected').removeClass('selected');

            // Resetting internal state representation
            currWord = '';
            positions = [];

            paintBoard();
        }
    });
});
