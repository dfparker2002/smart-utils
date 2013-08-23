<%@include file="/libs/wcm/global.jsp"%>
<%@page contentType="text/html;charset=UTF-8" %>
<head>
    <script src="/libs/cq/ui/resources/cq-ui.js" type="text/javascript"></script>
</head>
<body>
    <h2>Repository Query Reporter</h2>
    <div style="max-width: 470px">
        <p>
            Make a request to repository and return a result in a text format in browser. This very helpful when need to
            retrieve a list of specific node in flat list.
        </p>

        <form action="/services/smart-utils/repository-query-reporter" method="post">
            <br>
            Query: <input type="text" name="query">&nbsp;
            <select name="queryType">
                <option value="xpath">XPATH</option>
                <option value="sql">SQL</option>
                <option value="JCR-SQL2">JCR-SQL2</option>
                <option value="JCR-JQOM">JCR-JQOM</option>
            </select>
            <br>
            <br>
            <input type="submit" value="Query">
        </form>
    </div>
</body>