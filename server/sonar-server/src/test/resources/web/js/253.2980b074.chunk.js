webpackJsonp([253],{2113:function(e,t,n){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var a=n(8),o=(n.n(a),n(0)),r=(n.n(o),n(30)),i=n.n(r),c=n(2234),l=n(2297),s=n(256),p=n(2304),u=n(207),m=n(55),d=n(729),h=n(385),f=n(2),y=n(2717),b=(n.n(y),this&&this.__extends||function(){var e=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(e,t){e.__proto__=t}||function(e,t){for(var n in t)t.hasOwnProperty(n)&&(e[n]=t[n])};return function(t,n){function a(){this.constructor=t}e(t,n),t.prototype=null===n?Object.create(n):(a.prototype=n.prototype,new a)}}()),v=function(e){function t(){var t=null!==e&&e.apply(this,arguments)||this;return t.state={loading:!0,breadcrumbs:[],total:0,page:0},t.handleLoadMore=function(){var e=t.state,a=e.baseComponent,o=e.components,r=e.page;if(a&&o){var i=["VW","SVW"].includes(t.props.component.qualifier);n.i(m.b)(a.key,r+1,i,t.props.branch&&n.i(h.b)(t.props.branch)).then(function(e){t.mounted&&t.setState({components:o.concat(e.components),page:e.page,total:e.total})}).catch(function(e){t.mounted&&(t.setState({loading:!1}),n.i(m.a)(e).then(t.handleError))})}},t.handleError=function(e){t.mounted&&t.setState({error:e})},t}return b(t,e),t.prototype.componentDidMount=function(){this.mounted=!0,this.handleComponentChange()},t.prototype.componentDidUpdate=function(e){e.component!==this.props.component||e.branch!==this.props.branch?this.handleComponentChange():e.location!==this.props.location&&this.handleUpdate()},t.prototype.componentWillUnmount=function(){n.i(d.g)(),this.mounted=!1},t.prototype.handleComponentChange=function(){var e=this,t=this.props,a=t.branch,o=t.component;n.i(d.c)(o.key,o.breadcrumbs),this.setState({loading:!0});var r=["VW","SVW"].includes(o.qualifier);n.i(m.c)(o.key,r,a&&n.i(h.b)(a)).then(function(){n.i(d.a)(o),e.handleUpdate()}).catch(function(t){e.mounted&&(e.setState({loading:!1}),n.i(m.a)(t).then(e.handleError))})},t.prototype.loadComponent=function(e){var t=this;this.setState({loading:!0});var a=["VW","SVW"].includes(this.props.component.qualifier);n.i(m.d)(e,a,this.props.branch&&n.i(h.b)(this.props.branch)).then(function(e){t.mounted&&(["FIL","UTS"].includes(e.component.qualifier)?t.setState({loading:!1,sourceViewer:e.component,breadcrumbs:e.breadcrumbs,searchResults:void 0}):t.setState({loading:!1,baseComponent:e.component,components:e.components,breadcrumbs:e.breadcrumbs,total:e.total,page:e.page,sourceViewer:void 0,searchResults:void 0}))}).catch(function(e){t.mounted&&(t.setState({loading:!1}),n.i(m.a)(e).then(t.handleError))})},t.prototype.handleUpdate=function(){var e=this.props,t=e.component,n=e.location,a=n.query.selected,o=a||t.key;this.loadComponent(o)},t.prototype.render=function(){var e=this.props,t=e.branch,r=e.component,m=e.location,d=this.state,y=d.loading,b=d.error,v=d.baseComponent,g=d.components,E=d.breadcrumbs,_=d.total,w=d.sourceViewer,S=t&&n.i(h.b)(t),k=E.length>1,q=a("spacer-top",{"new-loading":y});return o.createElement("div",{className:"page page-limited"},o.createElement(i.a,{title:n.i(f.translate)("code.page")}),b&&o.createElement("div",{className:"alert alert-danger"},b),o.createElement(p.a,{branch:S,component:r,location:m,onError:this.handleError}),o.createElement("div",{className:"code-components"},k&&o.createElement(l.a,{branch:S,breadcrumbs:E,rootComponent:r}),void 0==w&&void 0!=g&&o.createElement("div",{className:q},o.createElement(c.a,{baseComponent:v,branch:S,components:g,rootComponent:r})),void 0==w&&void 0!=g&&o.createElement(u.a,{count:g.length,total:_,loadMore:this.handleLoadMore}),void 0!=w&&o.createElement("div",{className:"spacer-top"},o.createElement(s.default,{branch:S,component:w.key}))))},t}(o.PureComponent);t.default=v},2233:function(e,t,n){"use strict";function a(e){return"FIL"!==e.qualifier&&"UTS"!==e.qualifier||!e.path?e.name+"\n\n"+e.key:e.path+"\n\n"+e.key}function o(e){for(var t=e.slice(0).sort(),n=t[0],a=n.length,o=t[t.length-1],r=0;r<a&&n.charAt(r)===o.charAt(r);)r++;var i=n.substr(0,r),c=i.split(/[\s\\\/]/),l=c[c.length-1];return i.substr(0,i.length-l.length)}function r(e){var t=e.branch,n=e.component,r=e.rootComponent,p=e.previous,u=e.canBrowse,m=void 0!==u&&u,d="DIR"===n.qualifier&&p&&"DIR"===p.qualifier,h=d&&void 0!=p?o([n.name+"/",p.name+"/"]):"",f=h?i.createElement("span",null,i.createElement("span",{style:{color:"#777"}},h),i.createElement("span",null,n.name.substr(h.length))):n.name,y=null;if(n.refKey&&"SVW"!==n.qualifier)y=i.createElement(c.Link,{to:{pathname:"/dashboard",query:{id:n.refKey}},className:"link-with-icon"},i.createElement(s.a,{qualifier:n.qualifier})," ",i.createElement("span",null,f));else if(m){var b={id:r.key,branch:t};n.key!==r.key&&Object.assign(b,{selected:n.key}),y=i.createElement(c.Link,{to:{pathname:"/code",query:b},className:"link-with-icon"},i.createElement(s.a,{qualifier:n.qualifier})," ",i.createElement("span",null,f))}else y=i.createElement("span",null,i.createElement(s.a,{qualifier:n.qualifier})," ",f);return i.createElement(l.a,{title:a(n)},y)}t.a=r;var i=n(0),c=(n.n(i),n(9)),l=n(2305),s=n(92)},2234:function(e,t,n){"use strict";function a(e){var t=e.baseComponent,n=e.branch,a=e.components,l=e.rootComponent,s=e.selected;return o.createElement("table",{className:"data zebra"},o.createElement(c.a,{baseComponent:t,rootComponent:l}),t&&o.createElement("tbody",null,o.createElement(r.a,{branch:n,component:t,key:t.key,rootComponent:l}),o.createElement("tr",{className:"blank"},o.createElement("td",{colSpan:8}," "))),o.createElement("tbody",null,a.length?a.map(function(e,t,a){return o.createElement(r.a,{branch:n,canBrowse:!0,component:e,key:e.key,previous:t>0?a[t-1]:void 0,rootComponent:l,selected:e===s})}):o.createElement(i.a,null)))}t.a=a;var o=n(0),r=(n.n(o),n(2298)),i=n(2302),c=n(2303)},2297:function(e,t,n){"use strict";function a(e){var t=e.branch,n=e.breadcrumbs,a=e.rootComponent;return o.createElement("ul",{className:"code-breadcrumbs"},n.map(function(e,i){return o.createElement("li",{key:e.key},o.createElement(r.a,{branch:t,canBrowse:i<n.length-1,component:e,rootComponent:a}))}))}t.a=a;var o=n(0),r=(n.n(o),n(2233))},2298:function(e,t,n){"use strict";var a=n(8),o=(n.n(a),n(0)),r=(n.n(o),n(2233)),i=n(2300),c=n(2299),l=n(2301),s=this&&this.__extends||function(){var e=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(e,t){e.__proto__=t}||function(e,t){for(var n in t)t.hasOwnProperty(n)&&(e[n]=t[n])};return function(t,n){function a(){this.constructor=t}e(t,n),t.prototype=null===n?Object.create(n):(a.prototype=n.prototype,new a)}}(),p=function(e){function t(){return null!==e&&e.apply(this,arguments)||this}return s(t,e),t.prototype.componentDidMount=function(){this.handleUpdate()},t.prototype.componentDidUpdate=function(){this.handleUpdate()},t.prototype.handleUpdate=function(){var e=this;this.props.selected&&setTimeout(function(){e.handleScroll()},0)},t.prototype.handleScroll=function(){var e=this.node.getBoundingClientRect(),t=e.top,n=e.bottom;n>window.innerHeight-10?window.scrollTo(0,n-window.innerHeight+window.pageYOffset+10):t<200&&window.scrollTo(0,t+window.pageYOffset-200)},t.prototype.render=function(){var e=this,t=this.props,n=t.branch,s=t.component,p=t.rootComponent,u=t.selected,m=void 0!==u&&u,d=t.previous,h=t.canBrowse,f=void 0!==h&&h,y=["VW","SVW"].includes(p.qualifier),b="APP"===p.qualifier,v=null;if(!s.refKey||"SVW"===s.qualifier)switch(s.qualifier){case"FIL":case"UTS":v=o.createElement(l.a,{branch:n,component:s});break;default:v=o.createElement(c.a,{branch:n,component:s})}var g=y?[{metric:"releasability_rating",type:"RATING"},{metric:"reliability_rating",type:"RATING"},{metric:"security_rating",type:"RATING"},{metric:"sqale_rating",type:"RATING"},{metric:"ncloc",type:"SHORT_INT"}]:[b&&{metric:"alert_status",type:"LEVEL"},{metric:"ncloc",type:"SHORT_INT"},{metric:"bugs",type:"SHORT_INT"},{metric:"vulnerabilities",type:"SHORT_INT"},{metric:"code_smells",type:"SHORT_INT"},{metric:"coverage",type:"PERCENT"},{metric:"duplicated_lines_density",type:"PERCENT"}].filter(Boolean);return o.createElement("tr",{className:a({selected:m}),ref:function(t){return e.node=t}},o.createElement("td",{className:"thin nowrap"},o.createElement("span",{className:"spacer-right"},v)),o.createElement("td",{className:"code-name-cell"},o.createElement(r.a,{branch:n,component:s,rootComponent:p,previous:d,canBrowse:f})),g.map(function(e){return o.createElement("td",{key:e.metric,className:"thin nowrap text-right"},o.createElement("div",{className:"code-components-cell"},o.createElement(i.a,{component:s,metricKey:e.metric,metricType:e.type})))}))},t}(o.PureComponent);t.a=p},2299:function(e,t,n){"use strict";function a(e){var t=e.component,a=e.branch;return o.createElement(r.Link,{to:{pathname:"/dashboard",query:{branch:a,id:t.refKey||t.key}},className:"icon-detach",title:n.i(i.translate)("code.open_component_page")})}t.a=a;var o=n(0),r=(n.n(o),n(9)),i=n(2)},2300:function(e,t,n){"use strict";function a(e){var t=e.component,n=e.metricKey,a=e.metricType,c="TRK"===t.qualifier,l="releasability_rating"===n,s=c&&l?"alert_status":n,p=c&&l?"LEVEL":a,u=Array.isArray(t.measures)&&t.measures.find(function(e){return e.metric===s});return u?o.createElement(r.a,{measure:i({},u,{metric:{key:s,type:p}})}):o.createElement("span",null)}t.a=a;var o=n(0),r=(n.n(o),n(389)),i=this&&this.__assign||Object.assign||function(e){for(var t,n=1,a=arguments.length;n<a;n++){t=arguments[n];for(var o in t)Object.prototype.hasOwnProperty.call(t,o)&&(e[o]=t[o])}return e}},2301:function(e,t,n){"use strict";function a(e){var t=e.branch,a=e.component,l=function(e){e.preventDefault(),r.default.openComponent({branch:t,key:a.key})};return o.createElement("a",{className:"link-no-underline",onClick:l,title:n.i(c.translate)("component_viewer.open_in_workspace"),href:"#"},o.createElement(i.a,null))}t.a=a;var o=n(0),r=(n.n(o),n(212)),i=n(2661),c=n(2)},2302:function(e,t,n){"use strict";function a(){return o.createElement("tr",null,o.createElement("td",{colSpan:2},n.i(r.translate)("no_results")),o.createElement("td",{colSpan:6}))}t.a=a;var o=n(0),r=(n.n(o),n(2))},2303:function(e,t,n){"use strict";function a(e){var t=e.baseComponent,a=e.rootComponent,i="VW"===a.qualifier||"SVW"===a.qualifier,c="APP"===a.qualifier,l=i?[n.i(r.translate)("metric_domain.Releasability"),n.i(r.translate)("metric_domain.Reliability"),n.i(r.translate)("metric_domain.Security"),n.i(r.translate)("metric_domain.Maintainability"),n.i(r.translate)("metric","ncloc","name")]:[c&&n.i(r.translate)("metric.alert_status.name"),n.i(r.translate)("metric","ncloc","name"),n.i(r.translate)("metric","bugs","name"),n.i(r.translate)("metric","vulnerabilities","name"),n.i(r.translate)("metric","code_smells","name"),n.i(r.translate)("metric","coverage","name"),n.i(r.translate)("metric","duplicated_lines_density","short_name")].filter(Boolean);return o.createElement("thead",null,o.createElement("tr",{className:"code-components-header"},o.createElement("th",{className:"thin nowrap"}," "),o.createElement("th",null," "),l.map(function(e){return o.createElement("th",{key:e,className:"thin nowrap text-right code-components-cell"},t&&e)})))}t.a=a;var o=n(0),r=(n.n(o),n(2))},2304:function(e,t,n){"use strict";var a=n(0),o=(n.n(a),n(6)),r=(n.n(o),n(8)),i=(n.n(r),n(4)),c=(n.n(i),n(2234)),l=n(47),s=n(2),p=n(55),u=n(34),m=this&&this.__extends||function(){var e=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(e,t){e.__proto__=t}||function(e,t){for(var n in t)t.hasOwnProperty(n)&&(e[n]=t[n])};return function(t,n){function a(){this.constructor=t}e(t,n),t.prototype=null===n?Object.create(n):(a.prototype=n.prototype,new a)}}(),d=function(e){function t(){var t=null!==e&&e.apply(this,arguments)||this;return t.state={query:"",loading:!1},t.handleSearch=function(e){if(t.mounted&&t.checkInputValue(e)){var a=t.props,o=a.branch,r=a.component,i=a.onError;t.setState({loading:!0});var c=["VW","SVW","APP"].includes(r.qualifier),s=c?"SVW,TRK":"BRC,UTS,FIL";n.i(l.f)(r.key,{branch:o,q:e,s:"qualifier,name",qualifiers:s}).then(function(n){t.mounted&&t.checkInputValue(e)&&t.setState({results:n.components,selectedIndex:n.components.length>0?0:void 0,loading:!1})}).catch(function(a){t.mounted&&t.checkInputValue(e)&&(t.setState({loading:!1}),n.i(p.a)(a).then(i))})}},t}return m(t,e),t.prototype.componentWillMount=function(){this.handleSearch=n.i(i.debounce)(this.handleSearch,250)},t.prototype.componentDidMount=function(){this.mounted=!0},t.prototype.componentWillReceiveProps=function(e){e.location!==this.props.location&&this.setState({query:"",loading:!1,results:void 0,selectedIndex:void 0})},t.prototype.componentWillUnmount=function(){this.mounted=!1},t.prototype.checkInputValue=function(e){return this.input.value===e},t.prototype.handleSelectNext=function(){var e=this.state,t=e.selectedIndex,n=e.results;null!=n&&null!=t&&t<n.length-1&&this.setState({selectedIndex:t+1})},t.prototype.handleSelectPrevious=function(){var e=this.state,t=e.selectedIndex;null!=e.results&&null!=t&&t>0&&this.setState({selectedIndex:t-1})},t.prototype.handleSelectCurrent=function(){var e=this.props,t=e.branch,a=e.component,o=this.state,r=o.results,i=o.selectedIndex;if(null!=r&&null!=i){var c=r[i];c.refKey?this.context.router.push(n.i(u.b)(c.refKey)):this.context.router.push({pathname:"/code",query:{branch:t,id:a.key,selected:c.key}})}},t.prototype.handleKeyDown=function(e){switch(e.keyCode){case 13:e.preventDefault(),this.handleSelectCurrent();break;case 38:e.preventDefault(),this.handleSelectPrevious();break;case 40:e.preventDefault(),this.handleSelectNext()}},t.prototype.handleQueryChange=function(e){this.setState({query:e}),e.length<3?this.setState({results:void 0}):this.handleSearch(e)},t.prototype.handleInputChange=function(e){var t=e.currentTarget.value;this.handleQueryChange(t)},t.prototype.handleSubmit=function(e){e.preventDefault();var t=this.input.value;this.handleQueryChange(t)},t.prototype.render=function(){var e=this,t=this.props.component,o=this.state,i=o.query,l=o.loading,p=o.selectedIndex,u=o.results,m=null!=p&&null!=u?u[p]:void 0,d=r("code-search",{"code-search-with-results":null!=u}),h=r("search-box-input",{touched:i.length>0&&i.length<3});return a.createElement("div",{id:"code-search",className:d},a.createElement("form",{className:"search-box",onSubmit:this.handleSubmit.bind(this)},a.createElement("button",{className:"search-box-submit button-clean"},a.createElement("i",{className:"icon-search"})),a.createElement("input",{ref:function(t){return e.input=t},onKeyDown:this.handleKeyDown.bind(this),onChange:this.handleInputChange.bind(this),value:i,className:h,type:"search",name:"q",placeholder:n.i(s.translate)("search_verb"),maxLength:100,autoComplete:"off"}),l&&a.createElement("i",{className:"spinner spacer-left"}),a.createElement("span",{className:"note spacer-left"},n.i(s.translateWithParameters)("select2.tooShort",3))),null!=u&&a.createElement(c.a,{branch:this.props.branch,components:u,rootComponent:t,selected:m}))},t.contextTypes={router:o.object.isRequired},t}(a.PureComponent);t.a=d},2305:function(e,t,n){"use strict";function a(e){var t=e.children,n=e.title;return o.createElement("span",{className:"code-truncated",title:n},t)}t.a=a;var o=n(0);n.n(o)},2661:function(e,t,n){"use strict";function a(){return i}t.a=a;var o=n(0),r=n.n(o),i=r.a.createElement("svg",{width:"9",height:"14",viewBox:"0 0 288 448"},r.a.createElement("path",{fill:"#236a97",d:"M120 216v-112q0-3.5-2.25-5.75t-5.75-2.25-5.75 2.25-2.25 5.75v112q0 3.5 2.25 5.75t5.75 2.25 5.75-2.25 2.25-5.75zM288 304q0 6.5-4.75 11.25t-11.25 4.75h-107.25l-12.75 120.75q-0.5 3-2.625 5.125t-5.125 2.125h-0.25q-6.75 0-8-6.75l-19-121.25h-101q-6.5 0-11.25-4.75t-4.75-11.25q0-30.75 19.625-55.375t44.375-24.625v-128q-13 0-22.5-9.5t-9.5-22.5 9.5-22.5 22.5-9.5h160q13 0 22.5 9.5t9.5 22.5-9.5 22.5-22.5 9.5v128q24.75 0 44.375 24.625t19.625 55.375z"}))},2667:function(e,t,n){t=e.exports=n(27)(void 0),t.push([e.i,'.code-breadcrumbs{display:flex;flex-wrap:wrap}.code-breadcrumbs>li{padding:5px 5px 3px}.code-breadcrumbs>li:first-child{padding-left:0}.code-breadcrumbs>li:after{position:relative;top:-1px;padding-left:10px;color:#777;font-size:11px;content:">"}.code-breadcrumbs>li:last-child:after{display:none}.code-components-cell{min-width:80px;padding-left:30px!important;box-sizing:border-box}.code-truncated{display:inline-block;vertical-align:text-top;max-width:50vw;overflow:hidden;text-overflow:ellipsis;white-space:nowrap}.code-name-cell{max-width:0}.code-name-cell .code-truncated{max-width:100%}.code-search{margin-bottom:10px}.code-search-with-results+.code-components{display:none}.code-search .search-box{padding-right:10px}.code-search .search-box .note{vertical-align:middle;opacity:0;transition:opacity .3s ease}.code-search .search-box input.touched~.note{opacity:1}.code-components-header{position:-webkit-sticky;position:sticky;top:95px;background-color:hsla(0,0%,100%,.9)}',""])},2717:function(e,t,n){var a=n(2667);"string"==typeof a&&(a=[[e.i,a,""]]);var o={};o.transform=void 0;n(28)(a,o);a.locals&&(e.exports=a.locals)}});
//# sourceMappingURL=253.2980b074.chunk.js.map