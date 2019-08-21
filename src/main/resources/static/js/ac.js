// Waits for DOM to load before running
$(document).ready(() => {
    // Use jQuery to get reference to the HTML <ul> element with dropdown-content class. This will
    // represent our list of suggestions, which we will append to when we generate new ones.
    ac($("#st1"), $("ul.dropdown-content1"), "/ac1");
    ac($("#st2"), $("ul.dropdown-content2"), "/ac2");
    ac($("#st3"), $("ul.dropdown-content3"), "/ac3");
    ac($("#st4"), $("ul.dropdown-content4"), "/ac4");
});

function ac(street, suggestions, post) {
    street.keyup(event => {
        if (typeof(street.val()) !== 'undefined' && street.val() != "") {

            // Build the Javascript object that contains data for the POST request.
            const postParameters = {"input": street.val()};

            // Make a POST request to the "/validate" endpoint with the input information.
            $.post(post, postParameters, responseJSON => {

                // Parse the JSON response into a JavaScript object.
                const responseObject = JSON.parse(responseJSON);
                    
                suggestions.empty();
                //Repopulate the dropdown with the returned suggestions.
                for (let i = 0; i < responseObject.suggestions.length; i++) {
                    const txt = document.createElement("li");
                    txt.innerHTML = responseObject.suggestions[i];
                    suggestions.append(txt);
                }
            });
        } else {
            suggestions.empty();
        }
    })
}
