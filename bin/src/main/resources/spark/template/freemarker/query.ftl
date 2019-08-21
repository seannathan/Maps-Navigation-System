<#assign content>

<div class="header">
	<h1> Look at the stars! </h1>
</div>

<div class="body">
	<p> upload your stars in the terminal <br>
		watch out for errors!
	</p> <br>

	<p> query for neighbors <br>
	<form method="GET" action="/neighbors">
	<textarea name="ncommand" placeholder="fill in 'k starname' or 'k x y z'"></textarea><br>
  		<input type="submit">
	</form>
	</p>

	<p> query by radius <br>
	<form method="GET" action="/radius">
	<textarea name="rcommand" placeholder="fill in 'radius starname' or 'radius x y z'"></textarea><br>
  		<input type="submit">
	</form>
	</p>
</div>

</#assign>
<#include "main.ftl">
