/***********************************************
 * THEME TOGGLE
 ***********************************************/
document.getElementById("themeToggle").addEventListener("click", () => {
    document.body.classList.toggle("light");
});

/***********************************************
 * COPY GENERATED LINK
 ***********************************************/
document.getElementById("copyBtn").addEventListener("click", () => {
    const linkField = document.getElementById("generatedLink");
    linkField.select();
    document.execCommand("copy");
    alert("Link copied to clipboard!");
});

/***********************************************
 * CREATE: SHARE FILE OR TEXT
 ***********************************************/
document.getElementById("generateBtn").addEventListener("click", () => {
    const fileInput = document.getElementById("fileInput");
    const textValue = document.getElementById("textInput").value.trim();
    const generateBtn = document.getElementById("generateBtn");
    const linkContainer = document.getElementById("linkContainer");
    const generatedLink = document.getElementById("generatedLink");

    // Prepare data
    let formData = new FormData();
    formData.append("text", textValue);
    if (fileInput.files.length > 0) {
        formData.append("file", fileInput.files[0]);
    }

    // POST to /api/share
    fetch("/api/share", {
        method: "POST",
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("Network response was not ok: " + response.status);
            }
            return response.json();
        })
        .then(data => {
            console.log("Response data:", data);

            // 1) Hide Generate button
            generateBtn.classList.add("hidden");

            // 2) Show link container
            linkContainer.classList.remove("hidden");

            // 3) Display the returned link
            if (data.link) {
                generatedLink.value = data.link;
            } else {
                generatedLink.value = "No 'link' field in JSON response!";
            }
        })
        .catch(error => {
            console.error("Error in fetch:", error);
            alert("Error generating link: " + error.message);
        });
});

// Copy button
document.getElementById("copyBtn").addEventListener("click", () => {
    const linkField = document.getElementById("generatedLink");
    linkField.select();
    document.execCommand("copy");
    alert("Link copied to clipboard!");
});
/***********************************************
 * RETRIEVE: GET FILE OR TEXT VIA CODE
 ***********************************************/

document.getElementById("retrieveBtn").addEventListener("click", () => {
    const code = document.getElementById("codeInput").value.trim();
    if (!code) {
        alert("Please enter a valid code.");
        return;
    }

    fetch(`/api/retrieve/${code}`)
        .then((response) => {
            const disposition = response.headers.get("Content-Disposition");
            if (disposition && disposition.includes("attachment")) {
                // It's a file -> let the browser download it
                window.location.href = `/api/retrieve/${code}`;
                return {};
            }
            return response.json();
        })
        .then((data) => {
            if (Object.keys(data).length === 0) return; // File was handled or no data
            let textContent = data.text ? data.text.trim() : "";

            // If text is empty, show default message
            if (!textContent) {
                textContent = "api has retrieved/code only";
            }

            // 1) Replace full URL with /retrieve/...
            //    Example: if text has "http://mydomain.com/retrieve/abc123"
            //    we transform it to "/retrieve/abc123"

            // --- Simple approach: if you know the exact domain ---
            // textContent = textContent.replace(
            //   /https?:\/\/your-domain\.com\/retrieve\/(\S+)/g,
            //   "/retrieve/$1"
            // );

            // --- Generic approach: any domain with /retrieve/ ---
            textContent = textContent.replace(
                /(http?:\/\/[^/\s]+\/retrieve\/(\S+))/g,
                "/retrieve/$2"
            );

            // 2) Open new tab with line-wrapping
            const newWindow = window.open("/api/retrieve/${code}", "_blank");
            newWindow.document.write(`
        <html>
          <head>
            <title>Quixo Text</title>
            <style>
              body {
                font-family: Arial, sans-serif;
                padding: 20px;
                color: #333;
                background: #f0f0f0;
              }
              h2 {
                color: #007bff;
              }
              pre {
                background: #fff;
                padding: 10px;
                border: 1px solid #ccc;
                white-space: pre-wrap; /* wrap text */
                word-wrap: break-word;  /* break long words */
              }
            </style>
          </head>
          <body>
            <h2>Text/ File Shared</h2>
            <pre>${textContent}</pre>
          </body>
        </html>
      `);
            newWindow.document.close();
        })
        .catch((error) => {
            console.error("Error retrieving:", error);
            alert("Unable to retrieve content. Please check the code.");
        });
});

/***********************************************
 * DELETE: DELETE FILE OR TEXT VIA CODE
 ***********************************************/
document.getElementById("deleteBtn").addEventListener("click", () => {
    const code = document.getElementById("deleteCodeInput").value.trim();
    if (!code) {
        alert("Please enter a valid code.");
        return;
    }

    fetch(`/api/delete/${code}`, {
        method: "DELETE",
    })
        .then((response) => {
            const deleteResult = document.getElementById("deleteResult");
            if (response.ok) {
                deleteResult.textContent = "Successfully deleted the share.";
            } else {
                deleteResult.textContent = "No share found or failed to delete.";
            }
        })
        .catch((error) => {
            console.error("Error deleting:", error);
            alert("Unable to delete share. Please try again.");
        });
});
