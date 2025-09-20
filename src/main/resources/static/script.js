var WORDS;

const NUMBER_OF_GUESSES = 6;
let guessesRemaining = NUMBER_OF_GUESSES;
let currentGuess = [];
let nextLetter = 0;
let rightGuessString = "";
console.log(rightGuessString)

// create a global var called number of guesses, guessesremaining, cureent guess, nextletter. create a var called rightguessstring that picks a random word from the array of words from words.js. log the random word to your console.

function initBoard() {
    document.getElementById('content').style.display = 'block';
    let board = document.getElementById("game-board");
    for (let i = 0; i < NUMBER_OF_GUESSES; i++) {
        let row = document.createElement("div")
        row.className = "letter-row"
        for (let j = 0; j < 5; j++) {
            let box = document.createElement("div")
            box.className = "letter-box"
            row.appendChild(box)
        }
        board.appendChild(row)
    }
}
async function loadPageContent() {
    const spinner = document.getElementById('spinner');
    try {
        spinner.classList.remove('hidden');
        document.getElementById('content').style.display = 'none';
        const response = await fetch('/api/words');
        if (!response.ok) {
            throw new Error(`HTTP error: ${response.status}`);
        }
        WORDS = await response.json();
        console.log(JSON.stringify(WORDS));
        rightGuessString=WORDS.selectedValue;
        spinner.classList.add('hidden');
        initBoard();
    } catch (error) {
        console.error('Error fetching data:', error);
        // Handle error, e.g., display an error message
        document.getElementById('error-message').textContent = 'Failed to load content.';
        document.getElementById('error-message').style.display = 'block';
    }
}

// Call the function to initiate the process when the page loads
document.addEventListener('DOMContentLoaded', loadPageContent);


// create a function "initBoard" that creates a row for each guess (6 guesses) and it creates 5 boxes for each row. For every box, it appends it to a row.
// the function will then add each row to the board, where each row is given the class of letter-row and each box is labelled letter-box.

document.addEventListener('keyup', (e) => {
    if (guessesRemaining === 0) {
        return
    }

    let pressedKey = String(e.key)
    if (pressedKey === "Backspace" && nextLetter !== 0) {
        deleteLetter()
        return
    }

    if (pressedKey === "Enter") {
        checkGuess()
        return
    }

    let found = pressedKey.match(/[a-z]/gi)
    if (!found || found.length > 1) {
        return

    }
    insertLetter(pressedKey)
})

//add an event listener whenever a key is released. 
// if the user is out of guesses, stop the function. 
// if the backspace is pressed and its not the first letter, run the delete letter function. 
// if the user selects enter, run the checkGuess function. 
// If the user selects any button outside of A-Z or its longer than the 5 letters cap of the wordle game, stop the function

function insertLetter(pressedKey) {
    if (nextLetter === 5) {
        return
    }

    pressedKey = pressedKey.toLowerCase()
    let row = document.getElementsByClassName("letter-row")[6 - guessesRemaining]
    let box = row.children[nextLetter]
    box.textContent = pressedKey
    box.classList.add("filled-box")
    currentGuess.push(pressedKey)
    nextLetter += 1
}

//this function checks if here's still space in the guess for this letter,
// finds the appropriate row, and puts the letter in the box.

function deleteLetter() {
    let row = document.getElementsByClassName("letter-row")[6 - guessesRemaining]
    let box = row.children[nextLetter - 1]
    box.textContent = ""
    box.classList.remove("filled-box")
    currentGuess.pop()
    nextLetter -= 1
}

// this function will gets the correct row, finds the last box and empties it, and then resets the nextLetter counter.

function checkGuess() {
    let row = document.getElementsByClassName("letter-row")[6 - guessesRemaining]
    let guessString = ''
    let rightGuess = Array.from(rightGuessString)
    for (const val of currentGuess) {
        guessString += val
    }
    if (guessString.length !== 5) {
        alert("Not enough letters!")
        return
    }
    if(!WORDS.values.includes(guessString)){
        alert("Word not in list!")
        return
    }
    for (let i = 0; i < 5; i++) {
        let letterColor = ''
        let box = row.children [i]
        let letter = currentGuess[i]
        let letterPosition = rightGuess.indexOf(currentGuess[i])

// is lettin the current guess
        if (letterPosition === -1) {
            letterColor = 'grey'
        } else {
// now, letter is definitely in word
// if letter index and right guess index are the same
// letter is in the right position

            if (currentGuess[i] === rightGuess[i]) {
//shade green
                letterColor = 'green'
            } else {
//shade yellow
                letterColor = 'yellow'
            }
            rightGuess[letterPosition] = "#"
        }

        let delay = 250 * i
        setTimeout(() => {
//shade box
            box.style.backgroundColor = letterColor
            shadeKeyBoard(letter, letterColor)
        }, delay)
    }

    if (guessString === rightGuessString) {
        alert("You guessed right! Game over!")
        guessesRemaining = 0


    } else {
        guessesRemaining -= 1
        currentGuess = []
        nextLetter = 0
        if (guessesRemaining === 0) {
            alert("You've run out of guesses! Game over!")
            alert(`The right word was: "${rightGuessString}"`)
        }
    }
}

// this check function will make sure the guess is 5 letters
// make sure the guess is a valid list 
// checks each letter of the word and shades them with gray(incorrect letter), yellow(right letter, wrong placing) and green(right letter and position)
// if you get the correct word then the user gets alerted you're correct!


function shadeKeyBoard(letter, color) {
    for (const elem of document.getElementsByClassName("keyboard-button")) {
        if (elem.textContent === letter) {
            let oldColor = elem.style.backgroundColor
            if (oldColor === 'green') {
                return
            }

            if (oldColor === 'yellow' && color !== 'green') {
                return
            }

            elem.style.backgroundColor = color
            break
        }
    }


}

document.getElementById("keyboard-cont").addEventListener("click", (e) => {
    const target = e.target

    if (!target.classList.contains("keyboard-button")) {
        return
    }
    let key = target.textContent

    if (key === "Del") {
        key = "Backspace"
    }

    document.dispatchEvent(new KeyboardEvent("keyup", {'key': key}))
})// else shade the key gray