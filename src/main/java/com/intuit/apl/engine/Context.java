package com.intuit.apl.engine;

import com.intuit.apl.AuthZDecision;
import com.intuit.apl.Response;
import com.intuit.apl.model.Result;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * The context on which conditions are evaluated Contains the following 1. Subject 2. Resource 3.
 * Action 4. Environment 5. Obligation List 6. AuthZDecision
 * This defines the vocabulary of APL.
 */

class Context <
	Subject extends Map<String, String>, 
	Resource extends Map<String, String>, 
	Action extends Map<String, String>, 
	Environment extends Map<String, String>, 
	Obligation extends Map<String, String>>
{
  public static final String NULL = "NULL";
  public Map<String, String> subject, sub, s;
  public Map<String, String> resource, res, r;
  public Map<String, String> action, a;
  public Map<String, String> environment, env, e;
  public List<Obligation> obligationList, o;
  public List<Result> results;
  public AuthZDecision decision, d;
  public AuthZDecision permit = AuthZDecision.PERMIT;
  public AuthZDecision deny = AuthZDecision.DENY;
  public AuthZDecision indeterminate = AuthZDecision.INDETERMINATE;
  public Map<String, Object> request = new HashMap<>();
  public Response response;

  /**
   * This is primary constructor.
   * The objects passed are made available to APL policy file.
   *  
   * @param subject Subject
   * @param resource Resource
   * @param action Action
   * @param environment Environment
   * @param request Request
   * @param obligationList Obligation list
   * @param results Results
   * @param decision AuthZDecision
   */
  Context(Subject subject, Resource resource, Action action, Environment environment,
      List<Obligation> obligationList, List<Result> results, AuthZDecision decision) {
    this.subject = this.sub = this.s = subject;
    this.resource = this.res = this.r = resource;
    this.action = this.a = action;
    this.environment = this.env = this.e = environment;
    this.obligationList = this.o = obligationList;
    this.decision = this.d = decision;
    this.results = results;
  }
  
  /**
   * This is an overload of other constructor. This supports a request object be supplied by caller to be provided as context to APL.
   * Request is optional. If not provided an empty map is used.
   *  
   * @param subject Subject
   * @param resource Resource
   * @param action Action
   * @param environment Environment
   * @param request Request
   * @param response Response
   * @param results Results
   * @param decision AuthZDecision
   */
  Context(Subject subject, Resource resource, Action action, Environment environment, Map<String, Object> request,
          Response response, List<Result> results, AuthZDecision decision) {
    this(subject, resource, action, environment, (List<Obligation>) response.getObligations(), results, decision);
    this.response = response;
    if(null != request) {
      this.request = request;
    }
  }

  public void setSubject(Map<String, String> subject) {
    this.subject = subject;
  }

  public void setAction(Map<String, String> action) {
    this.action = action;
  }

  public void setA(Map<String, String> a) {
    this.a = a;
  }

  public Map<String, String> getSubject() {
    return subject;
  }

  public Map<String, String> getSub() {
    return sub;
  }

  public Map<String, String> getS() {
    return s;
  }

  public Map<String, String> getResource() {
    return resource;
  }

  public Map<String, String> getRes() {
    return res;
  }

  public Map<String, String> getR() {
    return r;
  }

  public Map<String, String> getAction() {
    return action;
  }

  public Map<String, String> getA() {
    return a;
  }

  public Map<String, String> getEnvironment() {
    return environment;
  }

  public Map<String, String> getEnv() {
    return env;
  }

  public Map<String, String> getE() {
    return e;
  }


  public boolean containsAnyIgnoreCase(ArrayList<String> source, String target) {
    String[] destination = target.split(",");
    if (destination.length == 0 || source.size() == 0) {
      return false;
    }
    for (String d : destination) {
      for (int i = 0; i < source.size(); i++) {
        if (equalsIgnoreCase(source.get(i), d)) {
          return true;
        }
      }
    }
    return false;
  }
  
  public boolean matchesAnyIgnoreCase(ArrayList<String> source, String target) {
		String[] targetStrElements = target.split(",");
		if (targetStrElements.length == 0 || source.size() == 0) {
			return false;
		}

		for (String srcStr : source) {
			Pattern p = Pattern.compile(srcStr);

			for (String d : targetStrElements) {
				if (p.matcher(d).matches()) {
					return true;
				}
			}
		}
		return false;
  }
  

  public static boolean equalsIgnoreCase(final String str1, final String str2) {
    if (str1 == null || str2 == null) {
      return str1 == str2;
    } else if (str1 == str2) {
      return true;
    } else if (str1.length() != str2.length()) {
      return false;
    } else {
      return str1.equalsIgnoreCase(str2);
    }
  }

  public boolean containsAny(ArrayList<String> source, String target) {
    String[] destination = target.split(",");
    if (destination.length == 0 || source.size() == 0) {
      return false;
    }
    return !CollectionUtils.isEmpty(source) && !CollectionUtils.isEmpty(Arrays.asList(destination))
        ? CollectionUtils.containsAny(source, Arrays.asList(destination))
        : false;
  }

  public String attrFirst(String category, String attributeName) {
    String[] destination = null;
    String target;
    if (category.equals("sub")) {
      target = subject.get(attributeName);
      if (target != NULL) {
        destination = target.split(",");
      }

    } else if (category.equals("res")) {
      target = res.get(attributeName);
      if (target != NULL) {
        destination = target.split(",");
      }
    }

    if (category.equals("env")) {
      target = res.get(attributeName);
      if (target != NULL) {
        destination = target.split(",");
      }
    }

    if (category.equals("action")) {
      target = res.get(attributeName);
      if (target != NULL) {
        destination = target.split(",");
      }
    }


    if (destination.length == 0) {
      return null;
    } else {
      return destination[0];
    }
  }

  public boolean containsAllIgnoreCase(ArrayList<String> source, String target) {
    String[] destination = target.split(",");


    for (String d : destination) {
      for (int i = 0; i < source.size(); i++) {
        if (!equalsIgnoreCase(source.get(i), d)) {
          return false;
        }
      }
    }
    return true;
  }

  public boolean verifyRequest(){
    return verifyRequest(subject) && verifyRequest(resource) && verifyRequest(action) && verifyRequest(environment);
  }

  private boolean verifyRequest(Map<String, String> map){
    if(CollectionUtils.isEmpty(map)){
      return true;
    }

    Map<String, String> verifyMap = getPrefixMap(map, "verify_");

    for(Map.Entry<String, String> entry : verifyMap.entrySet()){
      String verifyKey = entry.getKey();
      if(!map.containsKey(verifyKey)){
        return false;
      }
      if(map.get(verifyKey) == null){
        return false;
      }
      if(!matchRegex(entry.getValue(), map.get(verifyKey))){
        return false;
      }
    }
    return true;
  }

  private Map<String, String> getPrefixMap(Map<String, String> map, String prefix){
    return map.entrySet().stream().filter(entry -> entry.getKey().startsWith(prefix))
            .filter(entry -> StringUtils.hasText(entry.getValue()))
            .collect(Collectors.toMap(entry -> entry.getKey().replaceFirst(prefix, ""), Map.Entry::getValue));
  }

  private boolean matchRegex(String regex, String value){
    return Pattern.compile(regex).matcher(value).matches();
  }

  public boolean isAttrSet(String category, String attributeName) {

    if (category.equals("sub")) {

      if (subject.get(attributeName) != NULL) {
        return true;
      }

    }

    if (category.equals("res")) {

      if (resource.get(attributeName) != NULL) {
        return true;
      }
    }

    if (category.equals("env")) {
      if (resource.get(attributeName) != NULL) {
        return true;
      }
    }

    if (category.equals("action")) {
      if (action.get(attributeName) != NULL) {
        return true;
      }
    }

    return false;
  }

  /**
   * A dummy function to test in APL.
   *
   * @param temp A temporary string
   * @return boolean A dummy function for testing function usages in APL files
   */
  public boolean dummyFunction(String temp) {
    return temp.equals("test");
  }

  /**
   * Get all view Ids.
   *
   * @param fileId FileId
   * @return returning viewIds for a fileId Document.apl related functions
   */
  public String[] getAllViewIds(int fileId) {

    String[][] viewIds = {{"123", "124"}, {"125", "126"}};

    return viewIds[fileId];
  }

  public String[] getAllViewFolderIds(int folderId) {

    String[][] viewIds = {{"123", "124"}, {"125", "126"}};
    return viewIds[folderId];
  }

  /**
   * Get all edit ids.
   *
   * @param fileId FileId
   * @return returning EditIds for a fileId Document.apl related functions
   */
  public String[] getAllEditIds(int fileId) {

    String[][] viewIds = {{"123", "124"}, {"125", "126"}};

    return viewIds[fileId];
  }

  /**
   * Get all delete ids.
   *
   * @param fileId FileId
   * @return returning DeleteIds for a fileId Document.apl related functions
   */
  public String[] getAllDeleteIds(int fileId) {

    String[][] viewIds = {{"123", "124"}, {"125", "126"}};

    return viewIds[fileId];
  }

  /**
   * Get folder id.
   *
   * @param fileId FileId
   * @return returning folder Id for a fileId Document.apl related functions
   */
  public String getFolderId(int fileId) {
    // returning a default folder id for a fileId
    return "0";
  }
  
  public int parseInt(String intStr) {
	  return Integer.parseInt(intStr);
  }

  /**
   * Check if folder is viewable.
   *
   * @param folderId FolderId
   * @param id Id to be checked
   * @return boolean If the folder is viewable then return true else return false
   */
  public boolean checkIfFolderIsViewable(String folderId, String id) {
    return folderId.equalsIgnoreCase(id);
  }

  /**
   * Check if folder is editable.
   *
   * @param folderId FolderId
   * @param id Id to be checked
   * @return boolean If the folder is editable then return true else return false
   */

  public boolean checkIfFolderIsEditable(String folderId, String id) {
    return folderId.equalsIgnoreCase(id);
  }


  public class MyMap {
    private Map<String, String> map;

    MyMap(Map<String, String> map) {
      this.map = map;
    }

    public MyMap put(String key, String value) {
      map.put(key, value);
      return this;
    }
  }

  public class MyList {
    private List<String> list;

    MyList(List<String> list) {
      this.list = list;
    }

    public MyList add(String value) {
      list.add(value);
      return this;
    }
  }

  /**
   * New obligation.
   * This also adds the obligation to the obligation list automatically.
   *
   * @return obligation map The actions to be executed if the rule is fired
   */
  public MyMap newObligation() {
    Map<String, String> obligation = new HashMap<String, String>();
    obligationList.add((Obligation) obligation);
    return new MyMap(obligation);
  }

  public MyMap newAdvice() {
    Map<String, String> advice = new HashMap<>();
    response.getAdvices().add(advice);
    return new MyMap(advice);
  }
  public MyMap newRemediation() {
    Map<String, String> remediation = new HashMap<>();
    response.getRemediations().add(remediation);
    return new MyMap(remediation);
  }
  public MyMap newCause() {
    Map<String, String> cause = new HashMap<>();
    response.getCauses().add(cause);
    return new MyMap(cause);
  }
  public MyList newResult() {
    Result result = new Result();
    results.add(result);
    return new MyList(result.getResult());
  }
}
