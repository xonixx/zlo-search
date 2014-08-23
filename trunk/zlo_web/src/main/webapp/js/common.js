angular.module('common', [])
    .directive('customSubmit', function () {
        /*
         based on https://gist.github.com/maikeldaloo/5133963
         */
        return {
            restrict: 'A',
            link: function (scope, element, attributes) {
                // Get the form object.
                var form = scope[ attributes.name ];
                form.$submitted = false;
                element.removeClass('submitted');

                element.bind('submit', function (e) {
                    if (form.lastSubmitTime && form.lastSubmitTime > new Date().getTime() - 1000) // prevent double submit within 1sec
                        return false;
                    e.preventDefault();
                    form.lastSubmitTime = new Date().getTime();

                    form.$submitted = true;
                    element.addClass('submitted');

                    // Remove the class pristine from all form elements.
                    element.find('.ng-pristine').removeClass('ng-pristine');

                    // Set all the fields to dirty and apply the changes on the scope so that
                    // validation errors are shown on submit only.
                    angular.forEach(form, function (formElement, fieldName) {
                        // If the fieldname starts with a '$' sign, it means it's an Angular
                        // property or function. Skip those items.
                        if (fieldName[0] === '$') return;

                        formElement.$pristine = false;
                        formElement.$dirty = true;
                    });
                    scope.$apply();

                    // Do not continue if the form is invalid.
                    if (form.$invalid) {
                        // Focus on the first field that is invalid.
                        var $first = element.find('.ng-invalid').first();
                        $first.focus();
                        $first.select2 && $first.select2('open');

                        return false;
                    }

                    // From this point and below, we can assume that the form is valid.
                    scope.$eval(attributes.customSubmit);
                    form.$setPristine();
                    scope.$apply();
                    element.removeClass('submitted');
                });

                element.bind('keypress', function (event) {
                    if (attributes.customSubmitNoenterkey && event.which === 13) {
                        event.preventDefault();
                        return false;
                    }
                    if (event.which === 13 && element.filter(":visible").length && $(":focus:not(textarea)").length)
                        element.submit();
                });
            }
        };
    });
