var map = L.map('mapid').setView([52.2237276, 20.9940600], 18);

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
}).addTo(map);

L.marker([52.2237276, 20.9940600]).addTo(map)
    .bindPopup('Place of business')
    .openPopup();
