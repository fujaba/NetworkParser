ArtifactIndexer = function(){};
ArtifactIndexer.prototype.create = function(node){
	var item = document.createElement(node.tag);
	var tag = node.tag.toLowerCase();
	for (var key in node) {
		var k = key.toLowerCase();
		if (k === 'tag' || k.charAt(0) === '$' || k === 'model') {
			continue;
		}
		if(k=='value'){
			item.innerHTML = node[key];
			continue;
		}
		if(k.indexOf("-")>=0){
			item.style[key] = node[key];
		}else if(node[key] != null) {
			item.setAttribute(key, node[key]);
			item[key] = node[key];
			if(key==="className"){
				item.setAttribute("class", node[key]);
			}
		}
	}
	if (node.$parent) {
		node.$parent.appendChild(item);
	}
	if (node.model) {
		item.model = node.model;
	}
	return item;
};
ArtifactIndexer.prototype.init = function(json){
	for(var id in json) {
		this.showProject(json[id]);
	}
};
ArtifactIndexer.prototype.strZero = function(value, len){
	while(value.length<len) {
		value = "0"+value;
	}
	return value;
}
ArtifactIndexer.prototype.showProject = function(project){
	var group = this.create({tag: "div"});
	var head = this.create({tag:"h1", style:"clear: both;", value:project.name, $parent:group});
	var latestsnapshot, latestrelease;

	if(project.latest && project.latest.length > 0 && project.latestsnapshot && project.latestsnapshot.length > 0) {
		latestsnapshot = project.latestsnapshot;
		latestrelease = project.latest;
	} else {
		var len = [1,1,1];
		for(var i in project.versions) {
			var artifact = project.versions[i];
			artifact.project = project;
			
			var v=artifact.version;
			for(var i=0;i<v.length;i++) {
				if(v.charAt(i)=='.') {
					if(i-start>len[pos]) {
						len[pos] = i-start;
					}
					start=i+1;
					pos++;
				}
			}
			if(v.length-start>len[pos]) {
				len[pos] = v.length-start;
			}
		}
		var latestsnapshotNo=0, latestreleaseNo=0;
		for(var i in project.versions) {
			var artifact = project.versions[i];
			var tempVersion="";
			var pos=0, start=0;
			var v=artifact.version;
			var start=0;
			for(var i=0;i<v.length;i++) {
				if(v.charAt(i)=='.') {
					tempVersion = tempVersion + this.strZero(v.substring(start, i), len[pos]);
					start=i+1;
					pos++;
				}
			}
			tempVersion = tempVersion + this.strZero(v.substring(start), len[pos]);
			artifact.no = tempVersion;
	
			if(artifact.snapshot) {
				if(tempVersion>latestsnapshotNo) {
					latestsnapshotNo = tempVersion;
					latestsnapshot = v;
				}
			} else if(tempVersion>latestreleaseNo) {
				latestreleaseNo = tempVersion;
				latestrelease = v;
			}
		}
	}
	var latestsnapshotItem, latestreleaseItem;
	if(latestrelease) {
		latestreleaseItem = this.showArtifactHeader("Current-Release-Versions: "+latestrelease, group);
	}
	if(latestsnapshot) {
		latestsnapshotItem = this.showArtifactHeader("Current-Snapshot-Versions: "+latestsnapshot, group);
	}
	//var versions = this.create({tag:"div", value:"Versions", $parent: group});
	var versions = this.showArtifactHeader("Versions", group);
	for(var i in project.versions) {
		var artifact = project.versions[i];
		if(!artifact.classifier || artifact.classifier.length < 1) {
			continue;
		}
		var item = this.showArtifactHeader(artifact.version, versions)
		if(artifact.snapshot && artifact.version == latestsnapshot) {
			this.showArtifact(artifact, latestsnapshotItem, "latest-SNAPSHOT", project.name);
			this.showArtifact(artifact, item);
		} else if(!artifact.snapshot && artifact.version == latestrelease) {
			this.showArtifact(artifact, latestreleaseItem, "latest", project.name);
			this.showArtifact(artifact, item);
		} else {
			this.showArtifact(artifact, item);
		}
		
	}
	document.body.appendChild(group);
};
ArtifactIndexer.prototype.showArtifactHeader = function(textValue, group){
	var item = this.create({tag:"div", style:"clear: both;", $parent:group});
	var ample = this.create({tag:"div", value:"&#0054;", style:"cursor:pointer;margin-top:2px;float:left;font-size:large;font-weight:bold;font-family:Webdings;", $parent:item});
	var group = this.create({tag:"div", style:"float:left;", $parent:item});
	var text = this.create({tag:"div", style:"cursor:pointer;font-size:large;font-weight:bold;", value:textValue, $parent:group});
	var artifacts = this.create({tag:"div", $parent:group, style:"display:block;overflow:hidden;"});
	var that = this;
	text.ample = ample;
	text.open=true;
	text.artifacts = artifacts;
	
	
	var toggle = function(evt) {
		if(text.open) {
			text.ample.innerHTML ="&#0052;";
			that.slideToggle(text);
		} else {
			text.ample.innerHTML ="&#0054;";
			that.slideToggle(text);
		}
	};
	text.onclick = toggle;
	text.initHeight=-42;
	text.intVal = null;
	ample.onclick = toggle;
	return artifacts;
};

ArtifactIndexer.prototype.slideToggle = function(text) {
	window.clearInterval(text.intVal);
	if(text.initHeight==-42) {
		text.initHeight = text.artifacts.offsetHeight;
	}
	if(text.open) {
		var h = text.initHeight;
		text.intVal = setInterval(function(){
			h--;
			text.artifacts.style.height = h + 'px';
			if(h <= 0) {
				window.clearInterval(text.intVal);
				text.artifacts.style.display="none";
				text.open = !text.open;

			}
			}, 1);
	} else {
		var h = 0;
		open = true;
		text.artifacts.style.display="block";
		text.intVal = setInterval(function(){
			h++;
			text.artifacts.style.height = h + 'px';
			if(h >= text.initHeight) {
				window.clearInterval(text.intVal);
				text.artifacts.style.display="";
				text.open = !text.open;
			}
		}, 1);
	}
};
ArtifactIndexer.prototype.showArtifact = function(artifact, parent, path, file){
	if(artifact.info) {
		this.create({tag:"div", value:artifact.info, style:"border-radius: 6px;padding: 10px;background-color: #eee;", $parent:parent});
	}
	if(artifact.classifier) {
		var ul = this.create({tag:"ul",  $parent:parent});
		for(var i in artifact.classifier) {
			var classifier = artifact.classifier[i];
			var li = this.create({tag:"li",  $parent:ul});
			//style:"border-radius: 6px;padding: 10px;background-color: #eee;"
			this.create({tag:"a", href:this.getPath(artifact, classifier, path, file), value:classifier, $parent:li});		
		}
	}
};
ArtifactIndexer.prototype.getPath = function(artifact, classifier, path, file){
	if("pom" == classifier || "jar" == classifier) {
		classifier = "."+classifier;
	}else {
		classifier = "-"+classifier+".jar"
	}
	var project = artifact.project;
	if(!path) {
		path = artifact.version;
		if(artifact.snapshot) {
			path += "-SNAPSHOT";
		}
	}
	if(!path) {
		path="";
	}
	if(!file) {
		file=project.name+"-"+artifact.version;
		if(artifact.snapshot) {
			file += "-SNAPSHOT";
		}
	}
	return project.groupid.replace(".","/")+"/"+project.name+"/"+path+"/"+file+classifier;
	//	return artifact.groupid.replace(".","/")+"/"+artifact.artifactid+"/"+path+"/"+artifact.artifactid +file+classifier;
};