const slider = document.getElementById("myRange");
const output = document.getElementById("output");
output.value = slider.value; // Display the default slider value

// Update the current slider value (each time you drag the slider handle)
slider.oninput = function() {
    output.value = this.value;
};
console.log("hej");
