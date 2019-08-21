<form onsubmit="return false">
  <span> Enter street names or click the map to find intersections! Then click find path
  to highlight the shortest path.</span><br><br>
  <div class="dropdown">
  		<input type="text" placeholder="enter starting street" id="st1" rows: 1>
  			<ul id="myDropdown" class="dropdown-content1"></ul>
  </div>
  <div class="dropdown">
  		<input type="text" placeholder="enter starting cross street" id="st2" rows: 1>
  			<ul id="myDropdown" class="dropdown-content2"></ul>
  </div>
  <input type="submit" id="inter1" value="Find intersection!"><br><br>
  <div class="dropdown">
  		<input type="text" placeholder="enter target street" id="st3" rows: 1>
  			<ul id="myDropdown" class="dropdown-content3"></ul>
  </div>
  <div class="dropdown">
  		<input type="text" placeholder="enter target cross street" id="st4" rows: 1>
  			<ul id="myDropdown" class="dropdown-content4"></ul>
  </div>
  <input type="submit" id="inter2" value="Find intersection!"><br><br><br>
  <input type="submit" id="submit" value="Find path!">
</form>

<div id="candi"></div>