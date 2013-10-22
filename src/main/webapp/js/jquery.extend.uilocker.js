/**
 * jQuery uiLock extension.
 * @see http://www.jquery4u.com/plugins/lock-freeze-web-page-jquery/#.UD_F6NaPWPw
 */

(function($) {
    $.extend({
    	uiLock: function(content){
            if(content == 'undefined') content = '';
            $('<div></div>').attr('id', 'uiLockId').css({
                'position': 'absolute',
                'top': 0,
                'left': 0,
                'z-index': 1000,
                'opacity': 0.6,
                'width':'100%',
                'height':'100%',
                'color':'white',
                'background-color':'black'
            }).html(content).appendTo('body');
        },
        uiUnlock: function(){
            $('#uiLockId').remove();
        }
    });
})(jQuery);
