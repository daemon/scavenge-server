% if data and "alert_title" in data and "alert_message" in data:
  <div id="alert-message-modal" class="modal fade" role="dialog">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header modal-header-bar">
          <button type="button" class="close" data-dismiss="modal">&times;</button>
          <h4 class="modal-title">${data["alert_title"]}</h4>
        </div>
        <div class="modal-body">
          <p>${data["alert_message"]}</p>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">OK</button>
        </div>
      </div>
    </div>
  </div>
<script>
$(function() {
  $("#alert-message-modal").modal("show");
});
</script>
% endif