// Load the content of the source and destination files
fetch("/compare", {
    method: "POST",
    body: formData // Your form data with files
})
    .then(response => response.json())
    .then(data => {
        const sourceEditor = CodeMirror(document.getElementById("source-file"), {
            value: data.sourceContent,
            mode: "text/plain",
            lineNumbers: true,
            readOnly: true,
        });

        const destinationEditor = CodeMirror(document.getElementById("destination-file"), {
            value: data.destinationContent,
            mode: "text/plain",
            lineNumbers: true,
            readOnly: true,
        });

        // Apply syntax highlighting and diff visualization to the formattedDiff
        // You might use a library like diff2html here
    });
// Load the content of the source and destination files
const sourceFileContent = "..." // Load your content here
const destinationFileContent = "..." // Load your content here

// Create CodeMirror instances for source and destination files
const sourceEditor = CodeMirror(document.getElementById("source-file"), {
    value: sourceFileContent,
    mode: "text/plain", // Set the appropriate mode
    lineNumbers: true,
    readOnly: true,
});

const destinationEditor = CodeMirror(document.getElementById("destination-file"), {
    value: destinationFileContent,
    mode: "text/plain", // Set the appropriate mode
    lineNumbers: true,
    readOnly: true,
});

// Apply syntax highlighting and diff visualization
// You'll need to use diff2html or similar libraries here
