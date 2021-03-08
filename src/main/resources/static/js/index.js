//changing navbar color on scroll
window.addEventListener('scroll', function (e) {
  var nav = document.getElementById('nav');
  if (document.documentElement.scrollTop || document.body.scrollTop > window.innerHeight) {
          nav.classList.add('navbar-light');
          nav.classList.add('bg-light');
          nav.classList.remove('navbar-dark');
          nav.classList.remove('bg-dark');
    } else {
          nav.classList.add('bg-dark');
          nav.classList.add('navbar-dark');
          nav.classList.remove('navbar-light');
          nav.classList.remove('bg-light');
      }

});

document.getElementById('experience').addEventListener('click', function(){
      window.location.href = "/signup";
});
