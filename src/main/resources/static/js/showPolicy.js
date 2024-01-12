function showPolicy(policy) {
    // Hide all pop-ups
    const popups = document.getElementsByClassName('popup-content');
    for (let i = 0; i < popups.length; i++) {
        popups[i].style.display = 'none';
    }

    // Show the selected policy pop-up
    document.getElementById('popup-' + policy).style.display = 'block';
}