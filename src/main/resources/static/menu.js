async function registerKeys() {
    const fillState = await getData('/polyglot/api/');

    // register key access to "Add card menu"
    $(document).keyup(function (e) {
        if (e.key === "a") {
            window.location.href = "/polyglot/add.html";
        }
    });

    // register key-code access to levels, if non empty
    if (fillState[0] != 0) {
        $(document).keyup(function (e) {
            if (e.key === "1") {
                window.location.href = "/polyglot/niveau.html?level=1";
            }
        });
    }
    if (fillState[1] != 0) {

        $(document).keyup(function (e) {
            if (e.key === "2") {
                window.location.href = "/polyglot/niveau.html?level=2";
            }
        });
    }
    if (fillState[2] != 0) {
        $(document).keyup(function (e) {
            if (e.key === "3") {
                window.location.href = "/polyglot/niveau.html?level=3";
            }
        });
    }
    if (fillState[3] != 0) {
        $(document).keyup(function (e) {
            if (e.key === "4") {
                window.location.href = "/polyglot/niveau.html?level=4";
            }
        });
    }
    if (fillState[4] != 0) {
        $(document).keyup(function (e) {
            if (e.key === "5") {
                window.location.href = "/polyglot/niveau.html?level=5";
            }
        });
    }
}

async function showFillState() {

    const fillState = await getData('/polyglot/api/');

    $('#niveau0').text("Niveau 1  (" + fillState[0] + ")");
    if (fillState[0] == 0) {
        $('#niveau0').addClass("disabled");
    }
    $('#niveau1').text("Niveau 2  (" + fillState[1] + ")");
    if (fillState[1] == 0) {
        $('#niveau1').addClass("disabled");
    }
    $('#niveau2').text("Niveau 3  (" + fillState[2] + ")");
    if (fillState[2] == 0) {
        $('#niveau2').addClass("disabled");
    }
    $('#niveau3').text("Niveau 4  (" + fillState[3] + ")");
    if (fillState[3] == 0) {
        $('#niveau3').addClass("disabled");
    }
    $('#niveau4').text("Niveau 5  (" + fillState[4] + ")");
    if (fillState[4] == 0) {
        $('#niveau4').addClass("disabled");
    }
}

async function getData(url) {
    const response = await fetch(url);
    return response.json()
}