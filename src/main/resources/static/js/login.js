$(document).ready(function () {
    let navH = $('header').outerHeight();
    let footerH = $('.footer-copyright').outerHeight();

    let sumH = navH + footerH;

    $('.login').css('height', `calc(100vh - ${sumH}px)`);
});
