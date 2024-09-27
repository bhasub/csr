var table;
$(document).ready(function() {
    table = $('#unapproved-requests').DataTable();
    $('.row').attr('style','width:100%');
} );

function approveRequest(e){
    $("#flash_message").hide();

    var challengeId = e.id;
    var challengeName = e.name;
    $.ajax('/approve/' + challengeId,
        {
            contentType: 'application/json', // type of response data
            success: function (data,status,xhr) {   // success callback function
                $('#flash_message').html('<div class="alert alert-success"><a href="#" style="margin-left: 1em;" class="close" data-dismiss="alert" aria-label="close" title="close">×</a><strong>Success!</strong> Request approved for challenge "' + challengeName + '".</div>');
                $('#flash_message').fadeIn();
                table.row($("#row" + challengeId)).remove().draw();
                setTimeout(function () {
                    $('#flash_message').fadeOut();
                },2000);
            },
            error: function (jqXhr, textStatus, errorMessage) { // error callback
                $('#flash_message').html('<div class="alert alert-danger"><a href="#" style="margin-left: 1em;" class="close" data-dismiss="alert" aria-label="close" title="close">×</a><strong>Failure - </strong> Request could not be approved. </div>');
                $('#flash_message').fadeIn();
            }
        });
}

function handleComments(e) {
    $("#flash_message").hide();

    var challengeId = e.id;
    var challengeName = e.name;

    $.ajax('/viewcomments/' + challengeId,
        {
            contentType: 'application/json', // type of response data
            success: function (data,status,xhr) {   // success callback function
                $('.modal-title').html("Comment challenge <strong>'" + challengeName + "'</strong>");
                $('.modal-body').html(data);
                $('.modal').show();
                $('#modal-submit-btn').show();
            },
            error: function (jqXhr, textStatus, errorMessage) { // error callback
                $('#flash_message').html('<div class="alert alert-danger"><a href="#" style="margin-left: 1em;" class="close" data-dismiss="alert" aria-label="close" title="close">×</a><strong>Failure - </strong> Could not fetch the comment.</div>');
                $('#flash_message').fadeIn();
            }
        });
}

function submitComment() {
    $("#flash_message").hide();

    $("#comment_form").submit(function(e) {

        e.preventDefault(); // avoid to execute the actual submit of the form.

        var form = $(this);
        var url = form.attr('action');

        $.ajax({
            type: "POST",
            url: url,
            data: form.serialize(), // serializes the form's elements.
            success: function(data)
            {
                $('#flash_message').html('<div class="alert alert-success"><a href="#" style="margin-left: 1em;" class="close" data-dismiss="alert" aria-label="close" title="close">×</a><strong>Success!</strong> Comment added.</div>');
                $('#flash_message').fadeIn();
                setTimeout(function () {
                    $('#flash_message').fadeOut();
                },2000);
            },
            error: function (jqXhr, textStatus, errorMessage) { // error callback
                $('#flash_message').html('<div class="alert alert-danger"><a href="#" style="margin-left: 1em;" class="close" data-dismiss="alert" aria-label="close" title="close">×</a><strong>Failure - </strong> Could not post the comment. </div>');
                $('#flash_message').fadeIn();
            }
        });


    });
    closeModal();
    $('#comment_form').trigger('submit');
}

function viewDetails(e){
    $("#flash_message").hide();

    var elements = e.id.split('_');
    var user = elements[0];
    var storyName = elements[1];
    var storyId = elements[2];

    $.ajax('/viewdetails/type/admin/' + storyId,
        {
            contentType: 'application/json', // type of response data
            success: function (data,status,xhr) {   // success callback function
                $('.modal-title').html("<strong>Story '" + storyName + "' by '" + user + "' </strong>");
                $('.modal-body').html(data);
                $('.modal').show();
                $('#modal-submit-btn').hide();
            },
            error: function (jqXhr, textStatus, errorMessage) { // error callback
                $('#flash_message').html('<div class="alert alert-danger"><a href="#" style="margin-left: 1em;" class="close" data-dismiss="alert" aria-label="close" title="close">×</a><strong>Failure - </strong> Could not fetch the details for story <b>' + storyName +'</b> </div>');
                $('#flash_message').fadeIn();
            }
        });

}

function closeModal() {
    $('.modal').hide();
}

function closeImgModal() {
    $("#imgModal").hide();
}

function onImageClick(el){
    $("#flash_message").hide();

    var modal = $("#imgModal");
    var modalImg = $("#img01");
    modal.show();
    modalImg.prop("src",el.src);
}