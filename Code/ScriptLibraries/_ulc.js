// main JS
$(document).ready( function() {
	prettyPrint();

	$('.about').click( function() {
		$('#about').dialog( {
			modal : true,
			title : 'About OSnippets',
			draggable : false,
			resizable : false,
			width : 'auto',
			height : 600,
			buttons : {
				"OK" : function() {
					$(this).dialog('close')
				}
			}
		})
	})

	$('.datepicker').datepicker( {})

	$('.dptrigger').click( function() {
		var id = $(this).attr('rel')
		var el = document.getElementById(id)
		if (el)
			el.focus()
	})

	$('.chzn-select').chosen( {

	})

	if ($('.tags').length != 0) {

		jQuery.extend( {
			getTagsJSON : function() {
				var result = null;
				$.ajax( {
					url : "json_tags.xsp",
					async : false,
					success : function(data) {
						result = data;
					}
				});
				return result;
			}
		})

		$('.tags').select2( {
			placeholder : "Tags",
			tags : $.getTagsJSON()

		});
	}
})