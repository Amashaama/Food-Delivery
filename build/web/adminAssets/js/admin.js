/*
 * Admin Panel JavaScript
 * MetisMenu and sidebar functionality
 */

(function($) {
    'use strict';

    // MetisMenu initialization
    $(document).ready(function() {
        // Initialize MetisMenu for sidebar navigation
        $('#menu').metisMenu();
        
        // Ensure dropdowns work properly
        $('.metismenu .has-arrow').on('click', function(e) {
            e.preventDefault();
            var $this = $(this);
            var $submenu = $this.next('ul');
            
            if ($submenu.length) {
                if ($submenu.is(':visible')) {
                    $submenu.slideUp(300);
                    $this.removeClass('active');
                } else {
                    // Close other open submenus
                    $('.metismenu .has-arrow').not($this).removeClass('active');
                    $('.metismenu ul').not($submenu).slideUp(300);
                    
                    // Open clicked submenu
                    $submenu.slideDown(300);
                    $this.addClass('active');
                }
            }
        });
    });

})(jQuery);
