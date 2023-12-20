"use strict";

function reload() {
    $('.z-tabs').css('width', '100%');
    $('.z-tabs-content').css('width', '100%');
    $('.z-tabpanel').css('height', '100%');

    $(".message-history").each(function() {
      this.scrollTop = this.scrollHeight;
    });
}



function scrollLoadMessage() {
    var divScroll = $('.div-scroll-group');
    var maxScrollTop = divScroll.prop('scrollHeight') - divScroll.innerHeight();

    if(maxScrollTop - divScroll.height() < divScroll.scrollTop()) {
        divScroll.scrollTop(maxScrollTop);
    }
};

function scrollLoadDialog() {
    var divScroll = $('.div-scroll-group');
    var maxScrollTop = divScroll.prop('scrollHeight') - divScroll.innerHeight();
    divScroll.scrollTop(maxScrollTop+20);
};

$(document).ready(function(){ scrollLoadDialog();});


$(window).load(function() {
    $(".z-vlayout-inner").css("padding-bottom", "0");
    reload();
});

$(window).resize(function() {
    reload();
});

function scrollLoadMessage() {
    var divScroll = $('.div-scroll-group');
    var maxScrollTop = divScroll.prop('scrollHeight') - divScroll.innerHeight();

    if(maxScrollTop - divScroll.height() < divScroll.scrollTop()) {
        divScroll.scrollTop(maxScrollTop);
    }
};

function scroll() {
    var divScroll = $('.div-scroll-group');
    var maxScrollTop = divScroll.prop('scrollHeight') - divScroll.innerHeight();
    divScroll.scrollTop(maxScrollTop);
};