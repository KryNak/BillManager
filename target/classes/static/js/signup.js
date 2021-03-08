$('document').ready(function(){
    let navH = $('.navbar').outerHeight();
    let footerH = $('.footer-copyright').outerHeight();

    let sumH = navH + footerH;

    $('.signup').css("height", `calc(100vh - ${sumH}px)`)
});
